package edu.pszlagor.langchain.rag.application.assistant;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestPropertySource(locations = {"/application-test.properties"})
@SpringBootTest
class AssistantServiceTest {
    private final AssistantService service;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    @MockBean
    private AssistantAiService assistantAiService;

    @Autowired
    AssistantServiceTest(AssistantService service,
                         EmbeddingStore<TextSegment> embeddingStore,
                         EmbeddingModel embeddingModel) {
        this.service = service;
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @Test
    void shouldAnswerWithBlueWhenTwoSourcesContainTheInformationButOnlyOneMatchesID() {
        // given
        addToEmbeddingStore("The ball is blue", UUID.randomUUID());
        UUID id = UUID.randomUUID();
        String expectedMatchingText = "The ball is yellow";
        addToEmbeddingStore(expectedMatchingText, id);
        String expectedResponse = "response";
        when(assistantAiService.chat(any(), eq(expectedMatchingText), any())).thenReturn(expectedResponse);
        String question = "What is the color of the ball? Answer with only: 'blue' or 'yellow'.";
        // when
        String response = service.chat(new DocumentScopedQuestion(id.toString(), question));
        // then
        verify(assistantAiService).chat(question, expectedMatchingText, "testFallbackAnswer");
        assertThat(response).isEqualTo(expectedResponse);
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