package edu.pszlagor.langchain.documentbasedagent.adapter.http;

import edu.pszlagor.langchain.documentbasedagent.application.DocumentDto;
import edu.pszlagor.langchain.documentbasedagent.application.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class FileControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private EmbeddingService embeddingService;

    @Test
    void shouldSaveAFileWhenUploadingAFileAsMultipartFormData() throws Exception {
        // given
        String expectedId = "testId";
        byte[] fileContent = "Some content".getBytes();
        String fileName = "test.txt";
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName,
                "multipart/form-data", fileContent);
        when(embeddingService.saveDocument(any())).thenReturn(expectedId);
        // when
        this.mvc.perform(multipart("/api/files/upload").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedId));
        // then
        verify(embeddingService).saveDocument(eq(new DocumentDto(fileName, fileContent)));
    }

}