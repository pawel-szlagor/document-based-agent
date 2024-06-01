package edu.pszlagor.langchain.documentbasedagent.adapter.http;

import edu.pszlagor.langchain.documentbasedagent.application.DocumentDto;
import edu.pszlagor.langchain.documentbasedagent.application.EmbeddingService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final EmbeddingService embeddingService;

    public FileController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        DocumentDto documentDto = new DocumentDto(file.getOriginalFilename(), file.getBytes());
        return embeddingService.saveDocument(documentDto);
    }

}
