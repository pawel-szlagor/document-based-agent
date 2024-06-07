package edu.pszlagor.langchain.rag.application.answervalidator.impl;

import edu.pszlagor.langchain.rag.application.answervalidator.AiAnswerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class LLMDrivenAnswerValidatorImpl implements AiAnswerValidator {
    private static final Logger log = LoggerFactory.getLogger(LLMDrivenAnswerValidatorImpl.class);
    private final LLMDrivenAnswerValidatorAgent llmAgent;

    @Autowired
    public LLMDrivenAnswerValidatorImpl(LLMDrivenAnswerValidatorAgent llmAgent) {
        this.llmAgent = llmAgent;
    }

    @Override
    public boolean isValidAnswer(String question, String answer, String expectedAnswer) {
        LLMDrivenAnswerValidatorAgent.ValidatorResponse response = llmAgent.ask(question, answer, expectedAnswer);
        log.debug("Reasoning: {}", response.reason());
        return response.isValid();
    }
}
