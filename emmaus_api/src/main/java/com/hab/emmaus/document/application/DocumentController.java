package com.hab.emmaus.document.application;

import com.hab.emmaus.document.application.dto.CreateDocumentRequest;
import com.hab.emmaus.document.application.dto.DocumentResponse;
import com.hab.emmaus.shared.common_utils.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/documents")
@Tag(name = "documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/create")
    public ResponseEntity<?> createDocument(@RequestBody CreateDocumentRequest req){
        documentService.create(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/{public-id}")
    public ResponseEntity<?> updateDocument(
            @PathVariable("public-id") UUID publicId,
            @RequestBody CreateDocumentRequest req
    ){
        documentService.update(publicId,req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{public-id}")
    public ResponseEntity<?> deleteDocument(@PathVariable("public-id") UUID publicId){
        documentService.delete(publicId);
        return ResponseEntity.ok().build();
    }

    /** getters **/
    @GetMapping("/pages")
    public ResponseEntity<PageResponse<DocumentResponse>> getPagesOfDocuments(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "doc_type", required = false) String doc_type,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ){
       var res = documentService.getDocuments(doc_type, title, page, size);
       return ResponseEntity.ok(res);
    }
}
