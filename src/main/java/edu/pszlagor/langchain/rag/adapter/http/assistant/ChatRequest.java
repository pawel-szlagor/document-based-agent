package edu.pszlagor.langchain.rag.adapter.http.assistant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatRequest(@JsonProperty("documentId") String documentId, @JsonProperty("question") String question) {
}
