package com.hab.emmaus.staff_profile.application;

import com.hab.emmaus.shared.common_utils.PageResponse;
import com.hab.emmaus.staff_profile.application.dto.CreatStaffProfileRequest;
import com.hab.emmaus.staff_profile.application.dto.StaffProfileResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/staff-profiles")
@Tag(name = "staff-profiles")
@RequiredArgsConstructor
public class StaffProfileController {
    private final StaffProfileService staffProfileService;

    @PostMapping("/create")
    public ResponseEntity<?> createStaffProfile(@RequestBody @Valid CreatStaffProfileRequest request){
        staffProfileService.create(request);
        return ResponseEntity.ok().build();
    }

    //update
    @PostMapping("/update/{public-id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable("public-id") UUID publicId,
            @RequestBody @Valid CreatStaffProfileRequest request){
        staffProfileService.update(publicId,request);
        return ResponseEntity.ok().build();
    }

    //delete
    @DeleteMapping("/delete/{public-id}")
    public ResponseEntity<?> deleteProfile(@PathVariable("public-id") UUID publicId){
        staffProfileService.delete(publicId);
        return ResponseEntity.ok().build();
    }

    /** getters **/
    @GetMapping("/pages")
    public ResponseEntity<PageResponse<StaffProfileResponse>> getPagesOfStaffProfile(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "position", required = false) String position,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "department", required = false) String department,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ){
        var res = staffProfileService.getStaffProfiles(name,position,email,phone,department,page,size);
        return ResponseEntity.ok(res);
    }
}
