package edu.pszlagor.langchain.rag.adapter.http;

import edu.pszlagor.langchain.rag.application.document.exception.DocumentLoadingException;
import edu.pszlagor.langchain.rag.application.document.exception.InvalidDocumentException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ErrorHandlingControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {DocumentLoadingException.class, InvalidDocumentException.class})
    public ErrorResponse handleMyCustomException(RuntimeException ex) {
        return new ErrorResponseWithMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    public record ErrorResponseWithMessage(HttpStatusCode httpStatusCode, String message) implements ErrorResponse {
        @NotNull
        @Override
        public HttpStatusCode getStatusCode() {
            return httpStatusCode;
        }

        @NotNull
        @Override
        public ProblemDetail getBody() {
            return ProblemDetail.forStatusAndDetail(httpStatusCode, message);
        }
    }

}
