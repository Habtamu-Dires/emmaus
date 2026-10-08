package com.hab.emmaus.document.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Table(name = "document")
public class Document {

    @Id
    private Long id;
    private UUID publicId;
    private String documentType;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private LocalDate publicationDate;
    private Integer displayOrder;
    private Instant uploadedAt;

    @PersistenceCreator
    public Document(Long id, UUID publicId, String documentType, String title, String description, String url, String thumbnailUrl, LocalDate publicationDate, Integer displayOrder, Instant uploadedAt) {
        this.id = id;
        this.publicId = publicId;
        this.documentType = documentType;
        this.title = title;
        this.description = description;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.publicationDate = publicationDate;
        this.displayOrder = displayOrder;
        this.uploadedAt = uploadedAt;
    }

    private Document(){};


    //create
    public static Document create(String documentType, String title, String description, String url, String thumbnailUrl, LocalDate publishedDate, Integer displayOrder){
        Document document = new Document();
        document.publicId = UUID.randomUUID();
        document.documentType = documentType;
        document.title = title;
        document.description = description;
        document.url = url;
        document.thumbnailUrl = thumbnailUrl;
        document.publicationDate = publishedDate;
        document.displayOrder = displayOrder;
        document.uploadedAt = Instant.now();

        return document;
    }

    //update
    public void update(String documentType, String title, String description, String url, String thumbnailUrl, LocalDate publishedDate, Integer displayOrder){
        this.documentType = documentType;
        this.title = title;
        this.description = description;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.publicationDate = publishedDate;
        this.displayOrder = displayOrder;
    }
}
