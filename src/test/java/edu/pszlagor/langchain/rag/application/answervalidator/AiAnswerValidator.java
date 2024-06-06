package edu.pszlagor.langchain.rag.application.answervalidator;

public interface AiAnswerValidator {

    boolean isValidAnswer(String question, String answer, String expectedAnswer);
}
