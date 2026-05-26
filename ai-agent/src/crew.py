from crewai import Agent, Crew, Process, Task
from crewai.project import CrewBase, agent, crew, task
from tools.cocktail_tools import (
    search_cocktail_recipes,
    get_recipes_by_mood,
    get_seasonal_recommendations,
    get_food_pairings_for_recipe,
    get_popular_cocktails,
    get_available_ingredients,
)


@CrewBase
class CocktailAgentCrew:
    agents_config = 'config/agents.yaml'
    tasks_config = 'config/tasks.yaml'

    def __init__(self, model):
        self.model = model

    @agent
    def head_bartender(self) -> Agent:
        return Agent(
            config=self.agents_config['head_bartender'],
            verbose=True,
            llm=self.model,
            tools=[
                search_cocktail_recipes,
                get_recipes_by_mood,
                get_seasonal_recommendations,
                get_popular_cocktails,
                get_available_ingredients,
            ]
        )

    @agent
    def recipe_analyst(self) -> Agent:
        return Agent(
            config=self.agents_config['recipe_analyst'],
            verbose=True,
            llm=self.model,
            tools=[
                get_food_pairings_for_recipe,
                search_cocktail_recipes,
            ]
        )

    @task
    def understand_user_intent(self) -> Task:
        return Task(config=self.tasks_config['understand_user_intent'])

    @task
    def generate_response(self) -> Task:
        return Task(config=self.tasks_config['generate_response'])

    @crew
    def crew(self) -> Crew:
        return Crew(
            agents=self.agents,
            tasks=self.tasks,
            process=Process.sequential,
            verbose=True
        )
