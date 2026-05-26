import json
from crewai_tools import tool
from tools.backend_client import BackendClient

_client = BackendClient()


def _format_result(data: dict) -> str:
    """Format API result as a readable JSON string."""
    if not data:
        return "暂无数据"
    if isinstance(data, dict):
        result = data.get("data", data)
        if isinstance(result, dict) and "content" in result:
            items = result["content"]
            formatted = []
            for item in items:
                formatted.append({
                    "id": item.get("id"),
                    "name": item.get("name"),
                    "description": item.get("description", "")[:100],
                    "mood": item.get("mood"),
                    "sweetness": item.get("sweetness"),
                    "alcohol": item.get("alcohol"),
                })
            return json.dumps(formatted, ensure_ascii=False, indent=2)
        return json.dumps(result, ensure_ascii=False, indent=2)
    return json.dumps(data, ensure_ascii=False, indent=2)


@tool("search_cocktail_recipes")
def search_cocktail_recipes(keyword: str) -> str:
    """搜索鸡尾酒配方。根据关键词（如"莫吉托"、"威士忌"、"夏日"等）搜索平台中的配方。
    :param keyword: 搜索关键词
    :return: 搜索结果的JSON字符串
    """
    import asyncio
    try:
        loop = asyncio.get_event_loop()
    except RuntimeError:
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
    result = loop.run_until_complete(_client.search_recipes(keyword))
    return _format_result(result)


@tool("get_recipes_by_mood")
def get_recipes_by_mood(mood: str) -> str:
    """根据心情获取鸡尾酒配方。支持的心情: happy(开心), sad(失落), excited(兴奋), romantic(浪漫), tired(疲惫), celebrating(庆祝)。
    :param mood: 心情英文名
    :return: 配方列表的JSON字符串
    """
    import asyncio
    try:
        loop = asyncio.get_event_loop()
    except RuntimeError:
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
    result = loop.run_until_complete(_client.get_recipes_by_mood(mood))
    return _format_result(result)


@tool("get_seasonal_recommendations")
def get_seasonal_recommendations(limit: int = 5) -> str:
    """获取当前季节推荐的鸡尾酒。根据当前日期自动识别季节和节日。
    :param limit: 返回的推荐数量
    :return: 推荐列表的JSON字符串
    """
    import asyncio
    try:
        loop = asyncio.get_event_loop()
    except RuntimeError:
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
    result = loop.run_until_complete(_client.get_seasonal_recommendations(limit))
    return _format_result(result)


@tool("get_food_pairings_for_recipe")
def get_food_pairings_for_recipe(recipe_id: int) -> str:
    """获取某个鸡尾酒配方的推荐食物搭配。
    :param recipe_id: 配方ID
    :return: 食物搭配列表的JSON字符串
    """
    import asyncio
    try:
        loop = asyncio.get_event_loop()
    except RuntimeError:
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
    result = loop.run_until_complete(_client.get_food_pairings(recipe_id))
    return _format_result(result)


@tool("get_popular_cocktails")
def get_popular_cocktails(size: int = 5) -> str:
    """获取平台上最热门的鸡尾酒配方。
    :param size: 返回数量
    :return: 热门配方列表的JSON字符串
    """
    import asyncio
    try:
        loop = asyncio.get_event_loop()
    except RuntimeError:
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
    result = loop.run_until_complete(_client.get_popular_recipes(size))
    return _format_result(result)


@tool("get_available_ingredients")
def get_available_ingredients(ingredient_type: str = None) -> str:
    """获取平台可用的调酒材料列表。
    :param ingredient_type: 材料类型，可选: BASE(基酒), JUICE(果汁), SYRUP(糖浆), GARNISH(装饰), OTHER(其他)
    :return: 材料列表的JSON字符串
    """
    import asyncio
    try:
        loop = asyncio.get_event_loop()
    except RuntimeError:
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
    result = loop.run_until_complete(_client.get_ingredients(ingredient_type))
    return _format_result(result)
