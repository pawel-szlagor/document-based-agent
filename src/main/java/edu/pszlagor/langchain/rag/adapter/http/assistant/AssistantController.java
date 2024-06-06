package edu.pszlagor.langchain.rag.adapter.http.assistant;

import edu.pszlagor.langchain.rag.application.assistant.AssistantService;
import edu.pszlagor.langchain.rag.application.assistant.DocumentScopedQuestion;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AssistantController {
    private final AssistantService assistantService;

    public AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest message) {
        return assistantService.chat(new DocumentScopedQuestion(message.documentId(), message.question()));
    }
}
