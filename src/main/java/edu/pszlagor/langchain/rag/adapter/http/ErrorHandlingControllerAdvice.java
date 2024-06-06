package edu.pszlagor.langchain.rag.adapter.http;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice extends ResponseEntityExceptionHandler {


}
