package com.hab.emmaus.app_user.application.reset_token;

import com.hab.emmaus.app_user.application.dto.TextDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/update-email")
@Tag(name = "update-email")
@RequiredArgsConstructor
public class ResetEmailController {

 private final ResetService resetService;

 @PostMapping
 public ResponseEntity<?> updateEmail(
         @RequestBody EmailUpdateRequest req
 ){
     resetService.updateEmail(req.email());
     return ResponseEntity.ok().build();
 }

}
