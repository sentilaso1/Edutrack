from fastapi import FastAPI, Request
from pydantic import BaseModel
from langchain_core.messages import HumanMessage, AIMessage, SystemMessage
from runeterra.config import Config
from runeterra.models import create_llm
from runeterra.agent import ask, create_history
from runeterra.tools import get_available_tools
from dotenv import load_dotenv
from fastapi.middleware.cors import CORSMiddleware

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

class ChatRequest(BaseModel):
    message: str
    history: list 

@app.post("/chat")
def chat_endpoint(req: ChatRequest):
    chat_history = create_history()
    if req.history:
        chat_history += [HumanMessage(msg) if idx % 2 == 0 else AIMessage(msg)
                     for idx, msg in enumerate(req.history)]
    reply = ask(req.message, chat_history, llm)
    return {"reply": reply}
