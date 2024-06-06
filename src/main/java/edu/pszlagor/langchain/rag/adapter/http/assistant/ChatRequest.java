package edu.pszlagor.langchain.rag.adapter.http.assistant;

public record ChatRequest(String documentId, String question) {
}
