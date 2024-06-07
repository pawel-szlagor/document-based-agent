package edu.pszlagor.langchain.rag.adapter.http.document;

import edu.pszlagor.langchain.rag.application.document.DocumentDto;
import edu.pszlagor.langchain.rag.application.document.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
class DocumentController {
    private final DocumentService documentService;

    DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        var documentDto = new DocumentDto(file.getOriginalFilename(), file.getBytes());
        return documentService.saveDocument(documentDto);
    }

}
