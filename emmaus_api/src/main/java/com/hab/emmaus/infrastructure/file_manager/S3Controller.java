package com.hab.emmaus.infrastructure.file_manager;

import com.hab.emmaus.infrastructure.file_manager.dto.GeneratePresignedUrlRequest;
import com.hab.emmaus.infrastructure.file_manager.dto.PreSignedUrlDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
@Tag(name = "s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/generate-presigned-url")
    public ResponseEntity<PreSignedUrlDto> generatePresignedUrl(
            @RequestBody @Valid GeneratePresignedUrlRequest req
    ){
        var presignedUrl = s3Service.generatePresignedUrl(req);
        return ResponseEntity.ok(presignedUrl);
    }


    @GetMapping("/presigned-delete-url")
    public ResponseEntity<PreSignedUrlDto> getPresignedUrlToDelete(
            @RequestParam("file_name") String fileName,
            @RequestParam("folder") String folder
    ) {
        var presignedUrl = s3Service.getPresignedUrlToDelete(fileName, folder);
        return ResponseEntity.ok(presignedUrl);
    }

    //    @GetMapping("/presigned-url")
//    public ResponseEntity<PreSignedUrlDto> getPresignedUrl(
//            @RequestParam("file_name") String fileName,
//            @RequestParam("folder") String folder
//    ) {
//        PreSignedUrlDto presignedUrl = s3Service.getPreSignedUrl(fileName, folder);
//        return ResponseEntity.ok(presignedUrl);
//    }
}
