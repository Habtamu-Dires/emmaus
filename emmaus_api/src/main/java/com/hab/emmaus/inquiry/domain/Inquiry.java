package com.hab.emmaus.inquiry.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table(name = "inquiry")
public class Inquiry {

    @Id
    private Long id;
    private UUID publicId;
    private String type;
    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private String subject;
    private String message;
    private String status;
    private String remark;
    private Instant createdAt;
    private Instant updatedAt;


    @PersistenceCreator
    public Inquiry(Long id, UUID publicId, String type, String senderName, String senderEmail, String senderPhone, String subject, String message, String status, String remark, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.publicId = publicId;
        this.type = type;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.senderPhone = senderPhone;
        this.subject = subject;
        this.message = message;
        this.status = status;
        this.remark = remark;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private Inquiry() {}

    //create
    public static Inquiry create(String type, String senderName, String senderEmail, String senderPhone, String subject, String message, String status, String remark){
        Inquiry inquiry = new Inquiry();
        inquiry.publicId = UUID.randomUUID();
        inquiry.type = type;
        inquiry.senderName = senderName;
        inquiry.senderEmail = senderEmail;
        inquiry.senderPhone = senderPhone;
        inquiry.subject = subject;
        inquiry.message = message;
        inquiry.status = status;
        inquiry.remark = remark;
        inquiry.createdAt = Instant.now();

        return inquiry;
    }

    //update
    public void update(String status, String remark){
        this.status = status;
        this.remark = remark;
        this.updatedAt = Instant.now();
    }
}
