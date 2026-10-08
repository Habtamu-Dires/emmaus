package com.hab.emmaus.inquiry.application.dto;

public record UpdateInquiryRequest(
        String status,
        String remark
) {
}
