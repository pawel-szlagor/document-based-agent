package edu.pszlagor.langchain.rag.application;

import edu.pszlagor.langchain.rag.application.answervalidator.AiAnswerValidator;
import edu.pszlagor.langchain.rag.application.answervalidator.impl.ExactAiAnswerValidator;
import edu.pszlagor.langchain.rag.application.answervalidator.impl.VectorSimilarityAnswerValidatorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RAGIntTestConfig {

    @Bean
    public AiAnswerValidator exactAnswerValidator() {
        return new ExactAiAnswerValidator();
    }

    @Bean
    public AiAnswerValidator vectorSimilarityAnswerValidator() {
        return new VectorSimilarityAnswerValidatorImpl();
    }
}
