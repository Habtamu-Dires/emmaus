package com.hab.emmaus.app_user.application.auth;

import com.hab.emmaus.app_user.application.auth.dto.*;
import com.hab.emmaus.app_user.application.reset_token.ResetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "auth")
class AuthController {

    private final AuthService authService;
    private final ResetService resetService;


    @PostMapping("/login")
    public ResponseEntity<AuthTokens> login(@RequestBody @Valid AuthenticationRequest request){
        AuthTokens res = authService.login(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokens> refresh(@RequestBody RefreshRequest request){
        AuthTokens res = authService.refresh(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<Void> forgetPassword(@RequestBody @Valid ForgetPasswordRequest request){
        authService.requestPasswordReset(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request){
        authService.resetPassword(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/reset-email")
    public ResponseEntity<Void> resetEmail(@RequestBody @Valid ResetEmailRequest request){
        resetService.resetEmail(request.rawToken(),request.newEmail());
        return ResponseEntity.accepted().build();
    }


    @PostMapping
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordRequest req
    ){
        authService.changePassword(req);
        return ResponseEntity.accepted().build();
    }
}

