package edu.pszlagor.langchain.rag.adapter.embeddingstore.inmemory;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InMemoryEmbeddingStoreIntTest {
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    @Autowired
    InMemoryEmbeddingStoreIntTest(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @Test
    void shouldFilterEmbeddingByIdWhenBothDifferentIngestedDocumentsMatchQuery() {
        // given
        addToEmbeddingStore("The ball is blue", UUID.randomUUID());
        UUID id = UUID.randomUUID();
        String expectedMatchingText = "The ball is yellow";
        addToEmbeddingStore(expectedMatchingText, id);
        String question = "What is the color of the ball?";
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .filter(MetadataFilterBuilder.metadataKey("id").isEqualTo(id.toString()))
                .queryEmbedding(embeddingModel.embed(question).content())
                .maxResults(2)
                .minScore(0.0)
                .build();
        // when
        var matches = embeddingStore.search(searchRequest).matches();
        // then
        assertThat(matches).singleElement().matches(embedding -> embedding.embedded().text().equals(expectedMatchingText));
    }

    @Test
    void shouldFilterEmbeddingByIdWhenOtherIngestedDocumentMatchQuery() {
        // given
        String text = "The sky is blue.";
        addToEmbeddingStore(text, UUID.fromString("0d536551-52af-4503-9b5a-f87b93710943"));
        String question = "What is the color of the sky?";
        String notExistingId = "ad599b19-617b-4671-b73f-ec387e743c79";
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .filter(MetadataFilterBuilder.metadataKey("id").isEqualTo(notExistingId))
                .queryEmbedding(embeddingModel.embed(question).content())
                .maxResults(2)
                .minScore(0.0)
                .build();
        // when
        var matches = embeddingStore.search(searchRequest).matches();
        // then
        assertThat(matches).isEmpty();
    }


    private void addToEmbeddingStore(String text, UUID id) {
        var textSegment = TextSegment.from(text, Metadata.from("id", id));
        var embedding = embeddingModel.embed(textSegment).content();
        embeddingStore.add(embedding, textSegment);
    }
}
