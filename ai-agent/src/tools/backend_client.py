import httpx
import os


class BackendClient:
    """Async HTTP client for the Java Cocktail Maker backend API."""

    def __init__(self, base_url: str = None):
        self.base_url = (base_url or
                         os.getenv("BACKEND_API_URL", "http://localhost:8080/api"))
        self._client = None

    @property
    def client(self):
        if self._client is None:
            self._client = httpx.AsyncClient(timeout=10.0)
        return self._client

    async def search_recipes(self, keyword: str, size: int = 5) -> dict:
        resp = await self.client.get(
            f"{self.base_url}/recipes/search",
            params={"keyword": keyword, "page": 0, "size": size}
        )
        resp.raise_for_status()
        return resp.json()

    async def get_recipes_by_mood(self, mood: str, size: int = 5) -> dict:
        resp = await self.client.get(
            f"{self.base_url}/recipes/mood/{mood}",
            params={"page": 0, "size": size}
        )
        resp.raise_for_status()
        return resp.json()

    async def get_recipe_detail(self, recipe_id: int) -> dict:
        resp = await self.client.get(f"{self.base_url}/recipes/{recipe_id}")
        resp.raise_for_status()
        return resp.json()

    async def get_seasonal_recommendations(self, limit: int = 5) -> dict:
        resp = await self.client.get(
            f"{self.base_url}/recommendations/seasonal",
            params={"limit": limit}
        )
        resp.raise_for_status()
        return resp.json()

    async def get_food_pairings(self, recipe_id: int) -> dict:
        resp = await self.client.get(
            f"{self.base_url}/recommendations/food-pairings/{recipe_id}"
        )
        resp.raise_for_status()
        return resp.json()

    async def get_popular_recipes(self, size: int = 5) -> dict:
        resp = await self.client.get(
            f"{self.base_url}/recipes/popular",
            params={"page": 0, "size": size}
        )
        resp.raise_for_status()
        return resp.json()

    async def get_ingredients(self, ingredient_type: str = None, size: int = 20) -> dict:
        params = {"page": 0, "size": size}
        if ingredient_type:
            params["type"] = ingredient_type
        resp = await self.client.get(
            f"{self.base_url}/ingredients", params=params
        )
        resp.raise_for_status()
        return resp.json()

    async def close(self):
        if self._client:
            await self._client.aclose()
            self._client = None
