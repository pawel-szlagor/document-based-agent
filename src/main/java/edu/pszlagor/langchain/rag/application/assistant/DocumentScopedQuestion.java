package edu.pszlagor.langchain.rag.application.assistant;

import jakarta.validation.constraints.NotBlank;

public record DocumentScopedQuestion(@NotBlank(message = "Document ID must not be blank") String documentId,
                                     @NotBlank(message = "Question must not be blank") String question) {
}