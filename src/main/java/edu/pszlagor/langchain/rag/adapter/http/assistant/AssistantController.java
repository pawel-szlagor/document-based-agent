package edu.pszlagor.langchain.rag.adapter.http.assistant;

import edu.pszlagor.langchain.rag.application.assistant.AssistantService;
import edu.pszlagor.langchain.rag.application.assistant.DocumentScopedQuestion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class AssistantController {
    private final AssistantService assistantService;

    AssistantController(AssistantService assistantService) {
        this.assistantService = assistantService;
    }

    @Operation(summary = "Answers the given question based on the document with given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully answered the question using information included in the referenced document.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content)})
    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest message) {
        return assistantService.chat(new DocumentScopedQuestion(message.documentId(), message.question()));
    }
}
