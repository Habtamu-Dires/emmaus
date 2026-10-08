package com.hab.emmaus.inquiry.application;

import com.hab.emmaus.inquiry.application.dto.CreateInquiryRequest;
import com.hab.emmaus.inquiry.application.dto.InquiryResponse;
import com.hab.emmaus.inquiry.application.dto.UpdateInquiryRequest;
import com.hab.emmaus.shared.common_utils.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/inquiries")
@Tag(name = "inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/create")
    public ResponseEntity<?> createInquiry(@RequestBody CreateInquiryRequest request){
        inquiryService.create(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{public-id}")
    public ResponseEntity<?> updateInquiry(
            @PathVariable("public-id") UUID publicId,
            @RequestBody UpdateInquiryRequest request
    ){
        inquiryService.update(publicId,request);
        return ResponseEntity.ok().build();
    }

    /** getters *************************************************************** **/
    @GetMapping("/pages")
    public ResponseEntity<PageResponse<InquiryResponse>> getPagesOfInquiries(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "from_date", required = false)LocalDate fromDate,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ){
        var res = inquiryService.getPagesOfInquiry(type, fromDate,page, size);
        return ResponseEntity.ok(res);
    }
}
