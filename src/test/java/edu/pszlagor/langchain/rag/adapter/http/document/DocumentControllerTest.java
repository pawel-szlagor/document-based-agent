package edu.pszlagor.langchain.rag.adapter.http.document;

import edu.pszlagor.langchain.rag.application.document.DocumentDto;
import edu.pszlagor.langchain.rag.application.document.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestPropertySource(locations = {"/application-test.properties"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DocumentControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private DocumentService documentService;

    @Test
    void shouldSaveAFileWhenUploadingAFileAsMultipartFormData() throws Exception {
        // given
        String expectedId = "testId";
        byte[] fileContent = "Some content".getBytes();
        String fileName = "test.txt";
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName,
                "multipart/form-data", fileContent);
        when(documentService.saveDocument(any())).thenReturn(expectedId);
        // when
        this.mvc.perform(multipart("/api/files/upload").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedId));
        // then
        verify(documentService).saveDocument(eq(new DocumentDto(fileName, fileContent)));
    }

}