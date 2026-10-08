package com.hab.emmaus.inquiry.application.dto;


public record CreateInquiryRequest(
        String type,
        String senderName,
        String senderEmail,
        String senderPhone,
        String subject,
        String message,
        String status,
        String remark
){ }
