from langchain_core.language_models.chat_models import BaseChatModel
from langchain_groq import ChatGroq
from langchain_ollama import ChatOllama

from runeterra.config import Config, ModelConfig, ModelProvider

def create_llm(model_config: ModelConfig) -> BaseChatModel:
    if model_config.provider == ModelProvider.OLLAMA:
        return ChatOllama(
            model=model_config.name,
            temperature=model_config.temperature,
            num_ctx=Config.OLLAMA_CONTEXT_WINDOW,
            verbose=False,
            keep_alive=1,
        )
    elif model_config.provider == ModelProvider.GROQ:
        return ChatGroq(
            model=model_config.name,
            temperature=model_config.temperature,
            model_kwargs={
                "top_p": 0.1,
                "frequency_penalty": 0.0,
                "presence_penalty": 0.0,
            }
        )