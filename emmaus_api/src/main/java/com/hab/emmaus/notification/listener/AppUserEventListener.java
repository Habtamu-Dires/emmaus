package com.hab.emmaus.notification.listener;

import com.hab.emmaus.app_user.event.EmailResetEvent;
import com.hab.emmaus.app_user.event.PasswordResetEvent;
import com.hab.emmaus.app_user.event.AdminSetPasswordEvent;
import com.hab.emmaus.infrastructure.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppUserEventListener {

    private final EmailService emailService;

    @Value("${app.email.frontend-url}")
    private String frontendUrl;


    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(AdminSetPasswordEvent event){
        log.info("Password event received: {}", event);
        notifyAdminPasswordReset(event.email(),event.userName(), event.password());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(PasswordResetEvent event){
        log.info("Password reset event received: {}", event);
        this.triggerPasswordReset(event.email(),event.userName(),event.rawToken());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(EmailResetEvent event){
        log.info("Email reset event received: {}", event);
        this.triggerEmailReset(event.newEmail(),event.userName(),event.rawToken());
    }


    /*** Email Trigger Methods ***/
    public void notifyAdminPasswordReset(String userEmail, String userName, String tempPassword) {
        emailService.sendTemplateEmail(
                userEmail,
                "Welcome To Emmaus !!!",
                "mail/admin-password-set",
                Map.of("userName", userName, "temporaryPassword", tempPassword)
        );
    }

    public void triggerPasswordReset(String userEmail, String userName, String token) {

        String link = frontendUrl + "/reset-password?token=" + token;
        emailService.sendTemplateEmail(
                userEmail,
                "Reset Your Password",
                "mail/password-reset",
                Map.of("userName", userName, "resetUrl", link)
        );
    }

    public void triggerEmailReset(String newEmail, String userName, String token) {

        String link = frontendUrl + "/reset-email?token=" + token + "&email=" + newEmail;
        emailService.sendTemplateEmail(
                newEmail,
                "Use This Link To Reset Your Email",
                "mail/email-reset",
                Map.of("userName", userName, "resetUrl", link)
        );
    }

    public void triggerAccountVerification(String userEmail, String userName) {
        String token = UUID.randomUUID().toString();
        // Save hashed token in DB with expiration timestamp...

        String link = frontendUrl + "/verify-account?token=" + token;
        emailService.sendTemplateEmail(
                userEmail,
                "Verify Your Account",
                "mail/verify-account",
                Map.of("userName", userName, "confirmationUrl", link)
        );
    }

}
