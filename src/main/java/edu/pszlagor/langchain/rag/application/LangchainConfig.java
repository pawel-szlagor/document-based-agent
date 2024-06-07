package edu.pszlagor.langchain.rag.application;

import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class LangchainConfig {
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingModel embeddingModel,
                                                         EmbeddingStore<TextSegment> embeddingStore,
                                                         @Value("${assistance.embedding-store.max-segment-size:100}") int maxSegmentSizeInTokens,
                                                         @Value("${assistance.embedding-store.max-overlap-size:5}") int maxOverlapSizeInTokens) {
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(maxSegmentSizeInTokens, maxOverlapSizeInTokens, new OpenAiTokenizer()))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentTransformer(doc -> {
                    doc.metadata().put("id", UUID.randomUUID().toString());
                    return doc;
                })
                .build();
    }

    @Bean
    public DocumentParser documentParser() {
        return new ApacheTikaDocumentParser();
    }

}
