package edu.pszlagor.langchain.rag.adapter.http.document;

import edu.pszlagor.langchain.rag.application.document.DocumentDto;
import edu.pszlagor.langchain.rag.application.document.DocumentService;
import edu.pszlagor.langchain.rag.application.document.exception.DocumentLoadingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
class DocumentController {
    private final DocumentService documentService;

    DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary = "Uploads a file and stores in Embedding Store")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File successfully processed and saved under returned ID",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid file",
                    content = @Content)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            DocumentDto documentDto = new DocumentDto(file.getOriginalFilename(), file.getBytes());
            return documentService.saveDocument(documentDto);
        } catch (IOException e) {
            throw new DocumentLoadingException(file.getOriginalFilename(), e);
        }
    }

}
