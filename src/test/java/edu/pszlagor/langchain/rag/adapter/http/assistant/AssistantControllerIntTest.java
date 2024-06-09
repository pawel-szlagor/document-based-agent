package edu.pszlagor.langchain.rag.adapter.http.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.pszlagor.langchain.rag.application.assistant.AssistantService;
import edu.pszlagor.langchain.rag.application.assistant.DocumentScopedQuestion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = AssistantController.class)
@TestPropertySource(locations = {"/application-test.properties"})
class AssistantControllerIntTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private AssistantService assistantService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldInvokeUnderlyingServiceAndReturnItsResponse() throws Exception {
        // given
        String question = "question";
        String id = "id";
        String response = "response";
        var expectedServiceArgument = new DocumentScopedQuestion(id, question);
        when(assistantService.chat(eq(expectedServiceArgument))).thenReturn(response);
        // when
        this.mvc.perform(post("/api/v1/chat")
                        .content(objectMapper.writeValueAsString(new ChatRequest(id, question)))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(response));
        // then
        verify(assistantService).chat(eq(expectedServiceArgument));

    }

}