package edu.pszlagor.langchain.rag.application.answervalidator.impl;

import edu.pszlagor.langchain.rag.application.answervalidator.AiAnswerValidator;
import org.apache.commons.lang3.StringUtils;

public class ExactAiAnswerValidator implements AiAnswerValidator {
    @Override
    public boolean isValidAnswer(String question, String answer, String expectedAnswer) {
        return StringUtils.normalizeSpace(answer).equalsIgnoreCase(StringUtils.normalizeSpace(expectedAnswer));
    }
}
