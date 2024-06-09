package edu.pszlagor.langchain.rag.application.document;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.validation.ValidationException;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@TestPropertySource(locations = {"/application-test.properties"})
@SpringBootTest
class DocumentServiceTest {

    @SpyBean
    private EmbeddingStoreIngestor embeddingStoreIngestor;
    @Captor
    private ArgumentCaptor<Document> documentArgumentCaptor;
    @MockBean
    private EmbeddingStore<TextSegment> embeddingStore;
    private final DocumentService service;


    @Autowired
    DocumentServiceTest(DocumentService service) {
        this.service = service;
    }

    @Test
    void shouldParseAndIngestDocumentWhenAddingNewFile() throws IOException {
        // given
        File inputFile = ResourceUtils.getFile("classpath:test-ingest-file.txt");
        byte[] fileContent = FileUtils.readFileToByteArray(inputFile);
        // when
        String documentId = service.saveDocument(new DocumentDto("story-about-happy-carrot.txt", fileContent));
        // then
        verify(embeddingStoreIngestor).ingest(documentArgumentCaptor.capture());
        Document capturedDocument = documentArgumentCaptor.getValue();
        assertThat(capturedDocument.text().trim()).isEqualTo("Sample Text");
        assertThat(capturedDocument.metadata().toMap().size()).isEqualTo(1);
        assertThat(capturedDocument.metadata().getString("id")).isNotNull();
        assertThat(documentId).isNotBlank();
    }

    @Test
    void shouldValidationFailWhenInputDocumentIsEmpty() {
        // given
        byte[] fileContent = new byte[]{};
        // when
        ThrowingCallable savingDocInvocation = () -> service.saveDocument(new DocumentDto("story-about-happy-carrot.txt", fileContent));
        // then
        assertThatThrownBy(savingDocInvocation).isInstanceOf(ValidationException.class).hasMessageEndingWith("File must not be empty");
    }

    @NullAndEmptySource
    @ParameterizedTest
    void shouldValidationFailWhenInputDocumentNameIsEmpty(String fileName) {
        // given
        byte[] fileContent = new byte[]{1, 2, 3};
        // when
        ThrowingCallable savingDocInvocation = () -> service.saveDocument(new DocumentDto(fileName, fileContent));
        // then
        Assertions.assertThatThrownBy(savingDocInvocation).isInstanceOf(ValidationException.class).hasMessageEndingWith("File name must not be blank");
    }
}