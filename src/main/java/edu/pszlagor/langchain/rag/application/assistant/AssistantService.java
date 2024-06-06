package edu.pszlagor.langchain.rag.application.assistant;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@Service
public class AssistantService {
    private final AssistantAiService assistantAiService;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final int resultSize;
    private final double minScore;
    private final String fallbackAnswer;

    @Autowired
    public AssistantService(AssistantAiService assistantAiService,
                            EmbeddingStore<TextSegment> embeddingStore,
                            EmbeddingModel embeddingModel,
                            @Value("${assistance.embedding.results-size:5}") int resultSize,
                            @Value("${assistance.embedding.min-score:0.0}") double minScore,
                            @Value("${assistant.chat.fallback-answer}") String fallbackAnswer) {
        this.assistantAiService = assistantAiService;
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
        this.resultSize = resultSize;
        this.minScore = minScore;
        this.fallbackAnswer = fallbackAnswer;

    }

    public String chat(@Valid DocumentScopedQuestion question) {
        log.trace("Starting a chat with question: {} and document ID to look up: {}", question.question(), question.documentId());

        var embeddingSearchResult = lookUpEmbeddings(question);

        String information = transformToContextInformation(embeddingSearchResult);

        if (information.isBlank()) {
            log.debug("Falling back to default answer.");
            return fallbackAnswer;
        }

        return assistantAiService.chat(question.question(), information, fallbackAnswer);
    }

    private List<EmbeddingMatch<TextSegment>> lookUpEmbeddings(DocumentScopedQuestion question) {
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .filter(MetadataFilterBuilder.metadataKey("id").isEqualTo(question.documentId()))
                .queryEmbedding(embeddingModel.embed(question.question()).content())
                .maxResults(resultSize)
                .minScore(minScore)
                .build();
        var embeddingSearchResult = embeddingStore.search(searchRequest).matches();
        if (log.isDebugEnabled() && embeddingSearchResult.isEmpty()) {
            log.debug("No embedding matching given question: '{}' and documentId: '{}' was found.", question.question(), question.documentId());
        }
        return embeddingSearchResult;
    }

    private static String transformToContextInformation(List<EmbeddingMatch<TextSegment>> embeddingSearchResult) {
        return embeddingSearchResult
                .stream()
                .map(EmbeddingMatch::embedded)
                .map(TextSegment::text)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
