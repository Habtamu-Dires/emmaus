package com.hab.emmaus.inquiry.application.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record InquiryResponse(
        UUID publicId,
        String type,
        String senderName,
        String senderEmail,
        String senderPhone,
        String subject,
        String message,
        String status,
        String remark,
        Instant createdAt,
        Instant updatedAt
){ }
