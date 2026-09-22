package com.hab.emmaus.app_user.application.auth;

import com.hab.emmaus.app_user.application.auth.dto.LogoutRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sign-out")
@Tag(name = "sign-out")
@RequiredArgsConstructor
public class LogoutController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request){
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/all")
    public ResponseEntity<Void> logout(){
        authService.logout();
        return ResponseEntity.noContent().build();
    }
}
