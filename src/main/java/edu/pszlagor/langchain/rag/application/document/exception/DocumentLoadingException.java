package edu.pszlagor.langchain.rag.application.document.exception;

public class DocumentLoadingException extends RuntimeException {
    public DocumentLoadingException(String documentName, Throwable cause) {
        super("An error occurred while loading a document: %s".formatted(documentName), cause);
    }
}
