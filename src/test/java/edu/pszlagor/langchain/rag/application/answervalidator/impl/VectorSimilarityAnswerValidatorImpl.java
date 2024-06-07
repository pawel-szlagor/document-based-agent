package edu.pszlagor.langchain.rag.application.answervalidator.impl;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;
import edu.pszlagor.langchain.rag.application.answervalidator.AiAnswerValidator;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class VectorSimilarityAnswerValidatorImpl implements AiAnswerValidator {
    private final EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    private static final double minScore = 0.95;

    @Override
    public boolean isValidAnswer(String question, String answer, String expectedAnswer) {
        double cosineSimilarity = CosineSimilarity.between(getEmbed(answer), getEmbed(expectedAnswer));
        return cosineSimilarity > minScore;
    }

    private Embedding getEmbed(String answer) {
        return embeddingModel.embed(TextSegment.textSegment(answer)).content();
    }
}
