import os
import uuid
import time
import json
import asyncio
from contextlib import asynccontextmanager
from pydantic import BaseModel, Field
from typing import List, Optional
from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse, StreamingResponse
import uvicorn
from langchain_openai import ChatOpenAI
from crew import CocktailAgentCrew


# 模型配置（通过环境变量覆盖）
MODEL_TYPE = os.getenv("MODEL_TYPE", "openai")

OPENAI_API_BASE = os.getenv("OPENAI_API_BASE", "https://api.openai.com/v1")
OPENAI_CHAT_API_KEY = os.getenv("OPENAI_CHAT_API_KEY", "sk-your-key")
OPENAI_CHAT_MODEL = os.getenv("OPENAI_CHAT_MODEL", "gpt-4o-mini")

ONEAPI_API_BASE = os.getenv("ONEAPI_API_BASE", "http://localhost:3000/v1")
ONEAPI_CHAT_API_KEY = os.getenv("ONEAPI_CHAT_API_KEY", "sk-your-key")
ONEAPI_CHAT_MODEL = os.getenv("ONEAPI_CHAT_MODEL", "qwen-max")

OLLAMA_API_BASE = os.getenv("OLLAMA_API_BASE", "http://localhost:11434/v1")
OLLAMA_CHAT_API_KEY = os.getenv("OLLAMA_CHAT_API_KEY", "ollama")
OLLAMA_CHAT_MODEL = os.getenv("OLLAMA_CHAT_MODEL", "llama3.1:latest")

PORT = int(os.getenv("PORT", "8001"))

# 全局 LLM 实例
model = None


# --- Pydantic Models (OpenAI 兼容格式) ---

class Message(BaseModel):
    role: str
    content: str


class ChatCompletionRequest(BaseModel):
    messages: List[Message]
    stream: Optional[bool] = False


class ChatCompletionResponseChoice(BaseModel):
    index: int
    message: Message
    finish_reason: Optional[str] = None


class ChatCompletionResponse(BaseModel):
    id: str = Field(default_factory=lambda: f"chatcmpl-{uuid.uuid4().hex}")
    object: str = "chat.completion"
    created: int = Field(default_factory=lambda: int(time.time()))
    choices: List[ChatCompletionResponseChoice]
    system_fingerprint: Optional[str] = None


# --- 生命周期 ---

@asynccontextmanager
async def lifespan(app: FastAPI):
    global model
    try:
        print(f"正在初始化 LLM 模型 (type={MODEL_TYPE})...")
        if MODEL_TYPE == "oneapi":
            model = ChatOpenAI(
                base_url=ONEAPI_API_BASE,
                api_key=ONEAPI_CHAT_API_KEY,
                model=ONEAPI_CHAT_MODEL,
            )
        elif MODEL_TYPE == "ollama":
            model = ChatOpenAI(
                base_url=OLLAMA_API_BASE,
                api_key=OLLAMA_CHAT_API_KEY,
                model=OLLAMA_CHAT_MODEL,
            )
        else:
            model = ChatOpenAI(
                base_url=OPENAI_API_BASE,
                api_key=OPENAI_CHAT_API_KEY,
                model=OPENAI_CHAT_MODEL,
            )
        print("LLM 初始化完成")
    except Exception as e:
        print(f"LLM 初始化失败: {e}")
        raise
    yield
    print("AI Agent 服务正在关闭...")


app = FastAPI(title="意趣调酒平台 - AI Agent", lifespan=lifespan)


# --- Chat Completions 接口 ---

@app.post("/v1/chat/completions")
async def chat_completions(request: ChatCompletionRequest):
    if not model:
        raise HTTPException(status_code=500, detail="服务未初始化")

    try:
        query_prompt = request.messages[-1].content
        print(f"用户问题: {query_prompt}")

        inputs = {"topic": query_prompt}
        result = CocktailAgentCrew(model).crew().kickoff(inputs=inputs)
        formatted_response = str(result)
        print(f"AI 回复: {formatted_response[:200]}...")

        if request.stream:
            async def generate_stream():
                chunk_id = f"chatcmpl-{uuid.uuid4().hex}"
                lines = formatted_response.split('\n')
                for i, line in enumerate(lines):
                    chunk = {
                        "id": chunk_id,
                        "object": "chat.completion.chunk",
                        "created": int(time.time()),
                        "choices": [{
                            "index": 0,
                            "delta": {"content": line + '\n'},
                            "finish_reason": None
                        }]
                    }
                    yield f"data: {json.dumps(chunk, ensure_ascii=False)}\n\n"
                    await asyncio.sleep(0.3)
                final_chunk = {
                    "id": chunk_id,
                    "object": "chat.completion.chunk",
                    "created": int(time.time()),
                    "choices": [{
                        "index": 0,
                        "delta": {},
                        "finish_reason": "stop"
                    }]
                }
                yield f"data: {json.dumps(final_chunk, ensure_ascii=False)}\n\n"
                yield "data: [DONE]\n\n"

            return StreamingResponse(generate_stream(), media_type="text/event-stream")
        else:
            response = ChatCompletionResponse(
                choices=[
                    ChatCompletionResponseChoice(
                        index=0,
                        message=Message(role="assistant", content=formatted_response),
                        finish_reason="stop"
                    )
                ]
            )
            return JSONResponse(content=response.model_dump())

    except Exception as e:
        print(f"处理请求出错: {e}")
        raise HTTPException(status_code=500, detail=str(e))


# --- 健康检查 ---

@app.get("/health")
async def health():
    return {"status": "ok", "model_ready": model is not None}


if __name__ == "__main__":
    print(f"启动 AI Agent 服务，端口: {PORT}")
    uvicorn.run(app, host="0.0.0.0", port=PORT)
