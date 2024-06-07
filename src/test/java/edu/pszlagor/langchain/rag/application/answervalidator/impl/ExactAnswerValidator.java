package edu.pszlagor.langchain.rag.application.answervalidator.impl;

import edu.pszlagor.langchain.rag.application.answervalidator.AiAnswerValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ExactAnswerValidator implements AiAnswerValidator {
    @Override
    public boolean isValidAnswer(String question, String answer, String expectedAnswer) {
        return normalize(answer).equalsIgnoreCase(normalize(expectedAnswer));
    }

    private static String normalize(String input) {
        return StringUtils.normalizeSpace(input);
    }
}
