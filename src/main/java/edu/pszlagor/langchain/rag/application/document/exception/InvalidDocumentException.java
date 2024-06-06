package edu.pszlagor.langchain.rag.application.document.exception;

public class InvalidDocumentException extends RuntimeException {
    public InvalidDocumentException(String msg) {
        super(msg);
    }

    public InvalidDocumentException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
