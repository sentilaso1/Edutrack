import os
from dataclasses import dataclass
from enum import Enum

class ModelProvider(str, Enum):
    OLLAMA = "ollama"
    GROQ = "groq"

@dataclass
class ModelConfig:
    name: str
    temperature: float
    provider: ModelProvider


LLAMA_3 = ModelConfig("llama3", 0.0, ModelProvider.OLLAMA)
QWEN_3 = ModelConfig("qwen/qwen3-32b", 0.0, ModelProvider.GROQ)

class Config:
    MODEL = QWEN_3
    OLLAMA_CONTEXT_WINDOW = 2048
    
    class MySQL:
        HOST = os.getenv("MYSQL_HOST", "localhost")
        PORT = int(os.getenv("MYSQL_PORT", 3306))
        USER = os.getenv("MYSQL_USER", "root")
        PASSWORD = os.getenv("MYSQL_PASSWORD", "1234")
        DATABASE = os.getenv("MYSQL_DATABASE", "edutrack")
