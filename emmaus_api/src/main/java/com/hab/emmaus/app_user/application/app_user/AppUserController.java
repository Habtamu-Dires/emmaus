package com.hab.emmaus.app_user.application.app_user;


import com.hab.emmaus.app_user.application.dto.TextDto;
import com.hab.emmaus.app_user.application.app_user.dto.CreateUserRequest;
import com.hab.emmaus.app_user.application.app_user.dto.UserProfile;
import com.hab.emmaus.app_user.application.app_user.dto.UserResponse;
import com.hab.emmaus.app_user.domain.app_user.enums.UserStatus;
import com.hab.emmaus.shared.common_utils.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/identity")
@Tag(name = "identity")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService identityService;

    @PostMapping("/create-user")
    public ResponseEntity<TextDto> createUser(
            @RequestBody @Valid CreateUserRequest req
    ){
        var res = identityService.createUser(req);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/activate/{public-id}")
    public ResponseEntity<?> activateUser(
            @PathVariable("public-id") UUID publicId
    ){
        identityService.activate(publicId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/disable/{public-id}")
    public ResponseEntity<?> disableUser(
            @PathVariable("public-id") UUID publicId,
            @RequestBody @Valid TextDto remark
    ){
        identityService.disable(publicId, remark.text());
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/verify-email/{public-id}")
    public ResponseEntity<?> verifyPhone(
            @PathVariable("public-id") UUID publicId
    ){
        identityService.verifyPhone(publicId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/reject-user/{public-id}")
    public ResponseEntity<?> rejectUser(
            @PathVariable("public-id") UUID publicId
    ){
        identityService.reject(publicId);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/add-remark/{public-id}")
    public ResponseEntity<?> addUserRemark(
            @PathVariable("public-id") UUID publicId,
            @RequestBody @Valid TextDto remark
    ){
        identityService.addRemark(publicId, remark.text());
        return ResponseEntity.accepted().build();
    }

    @PutMapping
    public ResponseEntity<UserProfile> updateUserProfile(@RequestBody @Valid UserProfile profile){
        UserProfile res = identityService.updateProfile(profile);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{user-id}")
    public ResponseEntity<?> deleteUserIdentity(
            @PathVariable("user-id") UUID userPublicId
    ){
        identityService.deleteUserIdentity(userPublicId);
        return ResponseEntity.accepted().build();
    }


    /************* getters *****************/

    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserProfile>> getPagesOfUser(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        var res = identityService.getPagesOfUser(
               status, email,name, page,size
        );
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<UserProfile> getUserProfile(){
        var res = identityService.getUserProfile();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserProfile> getUserByPhone(@PathVariable String phone){
        UserProfile res = identityService.getUserByPhone(phone);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<UserResponse>> filterUsers(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        var res = identityService.filterUsers(branchId,name,phone,page,size);
        return ResponseEntity.ok(res);
    }


}
