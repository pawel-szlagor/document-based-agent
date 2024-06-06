package edu.pszlagor.langchain.rag.application.document;

import dev.langchain4j.data.document.BlankDocumentException;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import edu.pszlagor.langchain.rag.application.document.exception.DocumentLoadingException;
import edu.pszlagor.langchain.rag.application.document.exception.InvalidDocumentException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
@Validated
@Slf4j
public class DocumentService {
    private final DocumentParser documentParser;
    private final EmbeddingStoreIngestor embeddingStoreIngestor;

    public String saveDocument(@Valid DocumentDto document) {
        log.info("Adding new document: {} of size {} bytes.", document.fileName(), document.content().length);

        try (InputStream documentContent = new ByteArrayInputStream(document.content())) {

            Document parsedDocument = documentParser.parse(documentContent);
            log.debug("Document: {} parsed to {} bytes of text.", document.fileName(), parsedDocument.text().length() / 8);

            validateParsedDocument(parsedDocument);

            embeddingStoreIngestor.ingest(parsedDocument);
            String documentId = parsedDocument.metadata().getString("id");
            log.info("Document: {} ingested with id: {}.", document.fileName(), documentId);
            return documentId;
        } catch (IOException exception) {
            throw new DocumentLoadingException(document.fileName(), exception);
        } catch (BlankDocumentException exception) {
            throw new InvalidDocumentException("Document must not be blank!", exception);
        }
    }

    private void validateParsedDocument(Document parsedDocument) {
        if (parsedDocument.text().isEmpty()) {
            throw new InvalidDocumentException("Document must contain some text.");
        }
    }

}
