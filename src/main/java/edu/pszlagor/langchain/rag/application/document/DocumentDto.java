package edu.pszlagor.langchain.rag.application.document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record DocumentDto(@NotBlank(message = "File name must not be blank") String fileName,
                          @NotEmpty(message = "File must not be empty") byte[] content) {
}
