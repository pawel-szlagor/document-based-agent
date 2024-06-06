package edu.pszlagor.langchain.rag.application.assistant;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestPropertySource(locations = {"/application-test.properties"})
@SpringBootTest
class AssistantServiceTest {
    private static final String TEST_FALLBACK_ANSWER = "testFallbackAnswer";
    private final AssistantService service;
    private final EmbeddingModel embeddingModel;
    @MockBean
    private EmbeddingStore<TextSegment> embeddingStore;
    @MockBean
    private AssistantAiService assistantAiService;

    @Autowired
    AssistantServiceTest(AssistantService service,
                         EmbeddingModel embeddingModel) {
        this.service = service;
        this.embeddingModel = embeddingModel;
    }

    @Test
    void shouldConcatenateTwoMatchingEmbeddedTextsWhenBuildingInformationContextForAI() {
        // given
        String id = "some id";
        String question = "some question";
        String expectedContext = "segment1\nsegment2";
        String expectedResponse = "response";
        EmbeddingSearchRequest expectedSearchRequest = EmbeddingSearchRequest.builder()
                .filter(MetadataFilterBuilder.metadataKey("id").isEqualTo(id))
                .queryEmbedding(embeddingModel.embed(question).content())
                .maxResults(5)
                .minScore(0.5)
                .build();
        var matches = Stream.of("segment1", "segment2")
                .map(TextSegment::textSegment)
                .map(textSegment -> new EmbeddingMatch<>(0.6, "embeddingId", null, textSegment))
                .toList();
        when(embeddingStore.search(eq(expectedSearchRequest))).thenReturn(new EmbeddingSearchResult<>(matches));
        when(assistantAiService.chat(any(), any(), any())).thenReturn(expectedResponse);
        // when
        String response = service.chat(new DocumentScopedQuestion(id, question));
        // then
        verify(assistantAiService).chat(question, expectedContext, TEST_FALLBACK_ANSWER);
        assertThat(response).isEqualTo(expectedResponse);
    }

    @Test
    void shouldAnswerWithFallbackAnswerWhenNoMatchesFoundForQuery() {
        // given
        String id = "some id";
        String question = "some question";
        EmbeddingSearchRequest expectedSearchRequest = EmbeddingSearchRequest.builder()
                .filter(MetadataFilterBuilder.metadataKey("id").isEqualTo(id))
                .queryEmbedding(embeddingModel.embed(question).content())
                .maxResults(5)
                .minScore(0.5)
                .build();
        when(embeddingStore.search(eq(expectedSearchRequest))).thenReturn(new EmbeddingSearchResult<>(List.of()));
        // when
        String response = service.chat(new DocumentScopedQuestion(id, question));
        // then
        verifyNoInteractions(assistantAiService);
        assertThat(response).isEqualTo(TEST_FALLBACK_ANSWER);
    }

    @NullAndEmptySource
    @ParameterizedTest
    void shouldValidationFailWhenIdIsBlank(String id) {
        // given
        String question = "question";
        // when
        ThrowingCallable serviceInvocation = () -> service.chat(new DocumentScopedQuestion(id, question));
        // then
        assertThatThrownBy(serviceInvocation).hasMessageEndingWith("Document ID must not be blank");
    }

    @NullAndEmptySource
    @ParameterizedTest
    void shouldValidationFailWhenQuestionIsBlank(String question) {
        // given
        String id = "id";
        // when
        ThrowingCallable serviceInvocation = () -> service.chat(new DocumentScopedQuestion(id, question));
        // then
        assertThatThrownBy(serviceInvocation).hasMessageEndingWith("Question must not be blank");
    }

    private void addToEmbeddingStore(String text, UUID id) {
        var textSegment = TextSegment.from(text, Metadata.from("id", id));
        var embedding = embeddingModel.embed(textSegment).content();
        embeddingStore.add(embedding, textSegment);
    }
}