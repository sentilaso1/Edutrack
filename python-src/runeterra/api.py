from fastapi import FastAPI, Request
from pydantic import BaseModel
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from dotenv import load_dotenv
from fastapi.middleware.cors import CORSMiddleware
from typing import Literal, List

from runeterra.config import Config
from runeterra.models import create_llm
from runeterra.agent import ask, create_history
from runeterra.tools import get_available_tools
from runeterra.logging import log_panel, green_border_style

load_dotenv()

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

llm = create_llm(Config.MODEL)
llm = llm.bind_tools(get_available_tools())

class Message(BaseModel):
    role: Literal["user", "assistant"]
    content: str

class ChatRequest(BaseModel):
    message: str
    history: List[Message]

@app.post("/chat")
def chat_endpoint(req: ChatRequest):
    log_panel(
        title="Incoming API Request",
        content=f"Message: {req.message}\nHistory: {req.history}",
        border_style=green_border_style,
    )

    chat_history = create_history()
    if req.history:
        for msg in req.history:
            if msg.role == "user":
                chat_history.append(HumanMessage(content=msg.content))
            elif msg.role == "assistant":
                chat_history.append(AIMessage(content=msg.content))
    reply = ask(req.message, chat_history, llm)

    log_panel(
        title="API Response",
        content=f"Reply: {reply}",
        border_style=green_border_style,
    )
    return {"reply": reply}

@app.get("/ping")
def ping():
    return {"status": "ok"}
