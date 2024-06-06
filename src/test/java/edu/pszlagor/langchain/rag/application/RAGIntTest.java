package edu.pszlagor.langchain.rag.application;

import edu.pszlagor.langchain.rag.application.assistant.AssistantService;
import edu.pszlagor.langchain.rag.application.assistant.DocumentScopedQuestion;
import edu.pszlagor.langchain.rag.application.document.DocumentDto;
import edu.pszlagor.langchain.rag.application.document.DocumentService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = "langchain4j.open-ai.chat-model.temperature=0.0")
@SpringBootTest
public class RAGIntTest {
    private final DocumentService documentService;
    private final AssistantService assistantService;

    @Autowired
    public RAGIntTest(DocumentService documentService, AssistantService assistantService) {
        this.documentService = documentService;
        this.assistantService = assistantService;
    }

    @ValueSource(strings = {"story-about-happy-carrot.pdf", "story-about-happy-carrot.docx", "story-about-happy-carrot.txt"})
    @ParameterizedTest
    void shouldAnswerQuestionWhenEmbeddedFileContainsRelevantInfo(String fileName) throws IOException {
        // given
        File inputFile = ResourceUtils.getFile("classpath:" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(inputFile);
        // when
        String documentId = documentService.saveDocument(new DocumentDto(fileName, fileContent));
        String response = assistantService.chat(new DocumentScopedQuestion(documentId, "What are names of Charlie's friends excluding Charlie? Answer with their comma separated names in alphabetical order in the following format: name1,name2,name3,..."));
        //then
        assertThat(response.split(",")).containsExactlyInAnyOrder("Bella", "Percy", "Timmy");
    }

    @Test
    void shouldReturnFallbackAnswerWhenEmbeddedFileDoesNotContainRelevantInfo() throws IOException {
        // given
        File inputFile = ResourceUtils.getFile("classpath:story-about-happy-carrot.docx");
        byte[] fileContent = FileUtils.readFileToByteArray(inputFile);
        // when
        String documentId = documentService.saveDocument(new DocumentDto("test.docx", fileContent));
        String response = assistantService.chat(new DocumentScopedQuestion(documentId, "What is the legacy of John Doe?"));
        //then
        assertThat(response).isEqualTo("Unfortunately the specified document doesn't seem to contain any content related to this question.");
    }
}
