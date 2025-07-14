import os
import random
from dataclasses import dataclass
from enum import Enum
from pathlib import Path

class ModelProvider(str, Enum):
    OLLAMA = "ollama"
    GROQ = "groq"

@dataclass
class ModelConfig:
    name: str
    temperature: float
    provider: ModelProvider


LLAMA_3 = ModelConfig("llama3", 0.0, ModelProvider.OLLAMA)
LLAMA_3_3 = ModelConfig("qwen/qwen3-32b", 0.0, ModelProvider.GROQ)

class Config:
    SEED = 42
    MODEL = LLAMA_3_3
    OLLAMA_CONTEXT_WINDOW = 2048

    class Path:
        APP_HOME = Path(os.getenv("APP_HOME", Path(__file__).parent.parent))
        DATA_DIR = APP_HOME / "data"
    
    class MySQL:
        HOST = os.getenv("MYSQL_HOST", "localhost")
        PORT = int(os.getenv("MYSQL_PORT", 3306))
        USER = os.getenv("MYSQL_USER", "root")
        PASSWORD = os.getenv("MYSQL_PASSWORD", "1234")
        DATABASE = os.getenv("MYSQL_DATABASE", "edutrack")

def seed_everthing(seed: int = Config.SEED):
    random.seed(seed)