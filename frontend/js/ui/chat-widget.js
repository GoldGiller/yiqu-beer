/**
 * 意趣调酒平台 - AI 对话助手悬浮组件
 */
class ChatWidget {
    constructor() {
        this.isOpen = false;
        this.isMinimized = false;
        this.messages = this.loadHistory();
        this.suggestions = [
            '推荐一杯夏日清爽饮品',
            '我今天心情好，推荐什么？',
            '适合晚上喝的鸡尾酒',
            '莫吉托怎么做？',
            '帮我配点小吃',
        ];
        this.init();
    }

    init() {
        this.createWidget();
        this.bindEvents();
    }

    loadHistory() {
        try {
            const history = localStorage.getItem('ai_chat_history');
            return history ? JSON.parse(history) : [];
        } catch {
            return [];
        }
    }

    saveHistory() {
        if (this.messages.length > 50) {
            this.messages = this.messages.slice(-50);
        }
        localStorage.setItem('ai_chat_history', JSON.stringify(this.messages));
    }

    createWidget() {
        const container = document.getElementById('chat-widget-container');
        if (!container) return;

        container.innerHTML = `
            <!-- 悬浮按钮 -->
            <div id="chat-fab" class="fixed bottom-5 right-5 z-50 w-14 h-14 rounded-full flex items-center justify-center cursor-pointer shadow-lg"
                style="background: linear-gradient(45deg, var(--accent-gold), var(--accent-copper)); animation: pulse 2s infinite;">
                <span class="text-2xl">🤖</span>
            </div>

            <!-- 聊天面板 -->
            <div id="chat-panel" class="fixed bottom-20 right-5 z-50 hidden glass-card flex flex-col"
                style="width:380px; height:520px; border-radius:16px; overflow:hidden;">
                <!-- 头部 -->
                <div class="flex items-center justify-between px-4 py-3" style="background:rgba(212,175,55,0.15);">
                    <div class="flex items-center gap-2">
                        <span class="text-xl">🤖</span>
                        <span class="font-semibold text-sm" style="color:var(--accent-gold);">AI 调酒助手</span>
                    </div>
                    <div class="flex gap-2">
                        <button id="chat-clear" class="text-xs px-2 py-1 rounded hover:bg-white/10 transition" style="color:var(--text-gold);" title="清空对话">清空</button>
                        <button id="chat-close" class="text-lg leading-none px-1 hover:bg-white/10 rounded transition" style="color:var(--text-gold);">&times;</button>
                    </div>
                </div>

                <!-- 快捷建议 -->
                <div id="chat-suggestions" class="flex gap-2 px-4 py-2 overflow-x-auto" style="background:rgba(0,0,0,0.2);">
                    ${this.suggestions.map(s =>
                        `<button class="chat-suggestion whitespace-nowrap text-xs px-3 py-1 rounded-full border transition flex-shrink-0"
                            style="border-color:rgba(212,175,55,0.3); color:var(--text-gold);"
                            data-text="${s}">${s}</button>`
                    ).join('')}
                </div>

                <!-- 消息区域 -->
                <div id="chat-messages" class="flex-1 overflow-y-auto p-4 space-y-3" style="scroll-behavior:smooth;">
                    ${this.renderMessages()}
                </div>

                <!-- 输入区域 -->
                <div class="flex items-center gap-2 p-3" style="border-top:1px solid rgba(212,175,55,0.15);">
                    <input id="chat-input" type="text" placeholder="输入你的问题..."
                        class="flex-1 text-sm rounded-lg px-3 py-2 outline-none"
                        style="background:rgba(255,255,255,0.1); border:1px solid rgba(212,175,55,0.3); color:var(--text-light);">
                    <button id="chat-send" class="w-9 h-9 rounded-lg flex items-center justify-center transition"
                        style="background:linear-gradient(45deg, var(--accent-gold), var(--accent-copper));">
                        <span class="text-sm">📤</span>
                    </button>
                </div>
            </div>
        `;
    }

    renderMessages() {
        if (this.messages.length === 0) {
            return `<div class="text-center text-sm py-6" style="color:var(--text-gold); opacity:0.7;">
                👋 你好！我是你的AI调酒助手。<br>可以问我任何关于鸡尾酒的问题！
            </div>`;
        }
        return this.messages.map(m => `
            <div class="flex ${m.role === 'user' ? 'justify-end' : 'justify-start'}">
                <div class="max-w-[85%] px-3 py-2 rounded-lg text-sm" style="${
                    m.role === 'user'
                        ? 'background:rgba(212,175,55,0.2); border:1px solid rgba(212,175,55,0.3);'
                        : 'background:rgba(255,255,255,0.08); border:1px solid rgba(255,255,255,0.1);'
                }">
                    <div class="chat-message-content">${this.formatContent(m.content)}</div>
                </div>
            </div>
        `).join('');
    }

    formatContent(text) {
        return text
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.+?)\*/g, '<em>$1</em>')
            .replace(/`(.+?)`/g, '<code style="background:rgba(212,175,55,0.2);padding:1px 4px;border-radius:3px;">$1</code>')
            .replace(/\n/g, '<br>');
    }

    bindEvents() {
        document.getElementById('chat-fab').addEventListener('click', () => this.open());
        document.getElementById('chat-close').addEventListener('click', () => this.close());
        document.getElementById('chat-send').addEventListener('click', () => this.send());
        document.getElementById('chat-input').addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.send();
            }
        });
        document.getElementById('chat-clear').addEventListener('click', () => {
            this.messages = [];
            this.saveHistory();
            this.refreshMessages();
        });

        // Suggestion chips
        document.querySelectorAll('.chat-suggestion').forEach(btn => {
            btn.addEventListener('click', () => {
                document.getElementById('chat-input').value = btn.dataset.text;
                this.send();
            });
        });
    }

    open() {
        document.getElementById('chat-fab').style.display = 'none';
        document.getElementById('chat-panel').classList.remove('hidden');
        this.isOpen = true;
        this.scrollToBottom();
    }

    close() {
        document.getElementById('chat-fab').style.display = 'flex';
        document.getElementById('chat-panel').classList.add('hidden');
        this.isOpen = false;
    }

    addMessage(role, content) {
        this.messages.push({ role, content });
        this.saveHistory();
    }

    refreshMessages() {
        const msgContainer = document.getElementById('chat-messages');
        if (msgContainer) {
            msgContainer.innerHTML = this.renderMessages();
            this.scrollToBottom();
        }
    }

    scrollToBottom() {
        setTimeout(() => {
            const msgContainer = document.getElementById('chat-messages');
            if (msgContainer) {
                msgContainer.scrollTop = msgContainer.scrollHeight;
            }
        }, 100);
    }

    async send() {
        const input = document.getElementById('chat-input');
        const text = input.value.trim();
        if (!text) return;

        input.value = '';
        this.addMessage('user', text);
        this.refreshMessages();

        // 添加加载占位
        this.messages.push({ role: 'assistant', content: '正在思考中...' });
        this.refreshMessages();

        try {
            const apiMessages = [
                { role: 'system', content: '你是意趣调酒平台的AI助手，是一位资深调酒师。用中文热情地回答用户关于鸡尾酒的问题。' },
                ...this.messages.filter(m => m.role !== 'assistant' || m.content !== '正在思考中...')
                    .slice(-20)
                    .map(m => ({ role: m.role, content: m.content }))
            ];

            const response = await fetch('/ai/v1/chat/completions', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    messages: apiMessages,
                    stream: true
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            // Remove placeholder
            this.messages.pop();

            // 流式读取
            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let fullContent = '';
            let buffer = '';

            // Add streaming bubble
            this.messages.push({ role: 'assistant', content: '' });
            const streamIndex = this.messages.length - 1;

            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                buffer += decoder.decode(value, { stream: true });
                const lines = buffer.split('\n');
                buffer = lines.pop() || '';

                for (const line of lines) {
                    if (line.startsWith('data: ')) {
                        const data = line.slice(6).trim();
                        if (data === '[DONE]') continue;
                        try {
                            const json = JSON.parse(data);
                            const content = json.choices?.[0]?.delta?.content || '';
                            if (content) {
                                fullContent += content;
                                this.messages[streamIndex].content = fullContent;
                                this.refreshMessages();
                            }
                        } catch {}
                    }
                }
            }

            if (!fullContent) {
                this.messages[streamIndex].content = '抱歉，AI助手暂时无法回复，请稍后再试。';
            }
        } catch (e) {
            console.error('Chat error:', e);
            this.messages.pop(); // Remove placeholder
            this.messages.push({ role: 'assistant', content: '抱歉，连接AI助手失败，请检查网络后重试。' });
        }

        this.saveHistory();
        this.refreshMessages();
        document.getElementById('chat-input').focus();
    }
}

// 初始化
document.addEventListener('DOMContentLoaded', () => {
    new ChatWidget();
});
