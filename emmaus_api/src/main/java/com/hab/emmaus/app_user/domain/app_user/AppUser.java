package com.hab.emmaus.app_user.domain.app_user;



import com.hab.emmaus.app_user.domain.app_user.enums.UserRole;
import com.hab.emmaus.app_user.domain.app_user.enums.UserStatus;
import com.hab.emmaus.shared.exception.BusinessException;
import com.hab.emmaus.shared.exception.ErrorCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Table("app_user")
public class AppUser {

    @Id
    private Long id;

    private UUID publicId;

    private String email;
    private String passwordHash;
    private Boolean isTempPassword;

    private Set<UserRole> roles;
    private UserStatus status;


    private String firstName;
    private String lastName;
    private String profilePic;

    private Boolean isEmailVerified;

    private Instant createdAt;
    private Instant updatedAt;

    private String remark;


    @PersistenceCreator
    protected AppUser(
            Long id,
            UUID publicId,

            String email,
            String passwordHash,
            Boolean isTempPassword,

            Set<String> roles,
            UserStatus status,

            String firstName,
            String lastName,
            String profilePic,

            Boolean isEmailVerified,
            Instant createdAt,
            Instant updatedAt,
            String remark
    ){
        this.id = id;
        this.publicId = publicId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isTempPassword = isTempPassword;
        this.roles = roles.stream().map(UserRole::valueOf).collect(Collectors.toSet());
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePic = profilePic;
        this.isEmailVerified = isEmailVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.remark = remark;
    }

    private AppUser(){};

    /* ---------- Domain behavior ---------- */

    // register
    public static AppUser register(
            String firstName,
            String lastName,
            String email,
            Set<UserRole> roles,
            String passwordHash,
            String remark
    ) {
        AppUser user = new AppUser();
        user.publicId = UUID.randomUUID();
        user.firstName = firstName;
        user.lastName = lastName;
        user.email = email;
        user.passwordHash = passwordHash;
        user.isTempPassword = false;
        user.roles = roles;
        user.status = UserStatus.OTP_SENT;
        user.remark = remark;
        user.createdAt = Instant.now();
        user.updatedAt = Instant.now();

        return user;
    }

    // update
    public void update(
            String profilePic,
            String firstName,
            String lastName
    ) {
        this.profilePic = profilePic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.updatedAt = Instant.now();
    }

    public void verifyEmail(){
        this.isEmailVerified = true;
        this.markVerified();
    }


    public void markVerified(){
        this.status = UserStatus.VERIFIED;
        this.updatedAt = Instant.now();
    }

    public void markPending(){
        this.status = UserStatus.PENDING;
        this.updatedAt = Instant.now();
    }

    public void markProfileCreated(){
        this.status = UserStatus.PROFILE_CREATED;
        this.updatedAt = Instant.now();
    }

    public void approve() {
        if (this.status != UserStatus.PENDING
                && this.status != UserStatus.VERIFIED
                && this.status != UserStatus.REJECTED
                && this.status != UserStatus.SUSPENDED
        ) {
            throw new BusinessException(ErrorCode.PHONE_NOT_VERIFIED);
        }
        this.status = UserStatus.APPROVED;
        this.updatedAt = Instant.now();
    }

    public void reject(String remark) {
        this.status = UserStatus.REJECTED;
        this.updatedAt = Instant.now();
        this.remark = remark;
    }

    public void suspend(String remark){
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = Instant.now();
        this.remark = remark;
    }

    public void disable(String remark){
        this.status = UserStatus.DISABLED;
        this.updatedAt = Instant.now();
        this.remark = remark;
    }

    public void activate(){
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
        this.remark = "";
    }

    public void deactivate(){
        this.status = UserStatus.PENDING;
        this.updatedAt = Instant.now();
        this.remark = "";
    }


    public void changePassword(String newHash) {
        this.passwordHash = newHash;
        this.updatedAt = Instant.now();
    }

    public void changeEmail(String newEmail) {
        this.email = newEmail;
        this.updatedAt = Instant.now();
    }

    public void addRemark(String remark) {
        this.remark = remark;
        this.updatedAt = Instant.now();
    }


    public void setPasswordAsTemp() {this.isTempPassword = true;}
    public void setPasswordAsNotTemp() {this.isTempPassword = false;}
}

