package edu.pszlagor.langchain.rag.adapter.http;

import edu.pszlagor.langchain.rag.application.document.exception.DocumentLoadingException;
import edu.pszlagor.langchain.rag.application.document.exception.InvalidDocumentException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
class ErrorHandlingControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {DocumentLoadingException.class, InvalidDocumentException.class})
    public ErrorResponse handleDocumentExceptions(RuntimeException ex) {
        String errorMsg = "Document could not be processed";
        log.error(errorMsg, ex);
        return new ErrorResponseWithMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ErrorResponse handleOtherException(RuntimeException ex) {
        log.error("Server encountered an unexpected condition.", ex);
        return new ErrorResponseWithMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
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
