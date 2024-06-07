package edu.pszlagor.langchain.rag.application.answervalidator.impl;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
interface LLMDrivenAnswerValidatorAgent {
    @SystemMessage("""
            ### Instructions
            You are a strict validator.
            You will be provided with a question, an answer, and a expectedAnswer.
            Your task is to validate whether the answer is correct for the given question, based on the expectedAnswer.
             
            Follow these instructions:
            - Respond only true or false and always include the reason for your response
            - Respond with true if the answer is correct
            - Respond with false if the answer is incorrect
            - Respond with false if the answer is not clear or concise
             
            Your response must be a json object with the following structure:
            {
                "isValid": true,
                "reason": "The answer is correct because it is based on the reference provided."
            }
             
            ### Example
            Question: Is Madrid the capital of Spain?
            Answer: No, it's Barcelona.
            ExpectedAnswer: The capital of Spain is Madrid
            ###
            Response: {
                "isValid": false,
                "reason": "The answer is incorrect because the reference states that the capital of Spain is Madrid."
            }
            """)
    @UserMessage("""
            ###
            Question: {{question}}
            ###
            Answer: {{answer}}
            ###
            ExpectedAnswer: {{expectedAnswer}}
            ###
            """)
    ValidatorResponse ask(@V("question") String question, @V("answer") String answer, @V("expectedAnswer") String expectedAnswer);

    record ValidatorResponse(boolean isValid, String reason) {
    }
}
