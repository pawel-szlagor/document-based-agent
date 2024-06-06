package edu.pszlagor.langchain.rag.adapter.embeddingstore.inmemory;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InMemoryEmbeddingStoreConfig {

    @Bean
    public EmbeddingStore<TextSegment> inMemoryEmbeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }
}
