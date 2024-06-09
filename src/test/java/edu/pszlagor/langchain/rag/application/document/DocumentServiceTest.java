package edu.pszlagor.langchain.rag.application.document;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.validation.ValidationException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestPropertySource(locations = {"/application-test.properties"})
@SpringBootTest
class DocumentServiceTest {

    @MockBean
    private EmbeddingStore<TextSegment> embeddingStore;
    private final DocumentService service;
    private final Resource inputFile;


    @Autowired
    DocumentServiceTest(DocumentService service, @Value("classpath:story-about-happy-carrot.txt") Resource inputFile) {
        this.service = service;
        this.inputFile = inputFile;
    }

    @Test
    void shouldStoreFileContentAsEmbeddingWhenHandlingTxtFile() throws IOException {
        // given
        byte[] fileContent = inputFile.getContentAsByteArray();
        // when
        String documentId = service.saveDocument(new DocumentDto("story-about-happy-carrot.txt", fileContent));
        // then
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
    void shouldValidationFailWhenInputDocumentIsEmpty(String fileName) {
        // given
        byte[] fileContent = new byte[]{1, 2, 3};
        // when
        ThrowingCallable savingDocInvocation = () -> service.saveDocument(new DocumentDto(fileName, fileContent));
        // then
        Assertions.assertThatThrownBy(savingDocInvocation).isInstanceOf(ValidationException.class).hasMessageEndingWith("File name must not be blank");
    }
}