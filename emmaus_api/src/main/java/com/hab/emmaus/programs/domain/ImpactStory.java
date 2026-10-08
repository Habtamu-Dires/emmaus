package com.hab.emmaus.programs.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Table(name = "impact_story")
public class ImpactStory {

    @Id
    private Long id;
    private UUID publicId;
    private Long programId;
    private String beneficiaryName;
    private Integer age;
    private String gender;
    private String location;
    private String imageUrl;
    private String videoUrl;
    private String shortQuote;
    private String fullStory;
    private String status;
    private String remark;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;

    @PersistenceCreator
    public ImpactStory(Long id, UUID publicId, Long programId, String beneficiaryName, Integer age, String gender, String location, String imageUrl, String videoUrl, String shortQuote, String fullStory, String status, String remark, Integer displayOrder, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.publicId = publicId;
        this.programId = programId;
        this.beneficiaryName = beneficiaryName;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.shortQuote = shortQuote;
        this.fullStory = fullStory;
        this.status = status;
        this.remark = remark;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private ImpactStory() {}


    //create
    public static ImpactStory create(Long programId, String beneficiaryName, Integer age, String gender, String location, String imageUrl, String videoUrl, String shortQuote, String fullStory, String status, String remark, Integer displayOrder){
        ImpactStory impactStory = new ImpactStory();
        impactStory.programId = programId;
        impactStory.publicId = UUID.randomUUID();
        impactStory.beneficiaryName = beneficiaryName;
        impactStory.age = age;
        impactStory.gender = gender;
        impactStory.location = location;
        impactStory.imageUrl = imageUrl;
        impactStory.videoUrl = videoUrl;
        impactStory.shortQuote = shortQuote;
        impactStory.fullStory = fullStory;
        impactStory.status = status;
        impactStory.remark = remark;
        impactStory.displayOrder = displayOrder;
        impactStory.createdAt = Instant.now();
        impactStory.updatedAt = Instant.now();

        return impactStory;
    }

    //update
    public void update(String beneficiaryName, Integer age, String gender, String location, String imageUrl, String videoUrl, String shortQuote, String fullStory, String status, String remark, Integer displayOrder){
        this.beneficiaryName = beneficiaryName;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.shortQuote = shortQuote;
        this.fullStory = fullStory;
        this.status = status;
        this.remark = remark;
        this.displayOrder = displayOrder;
        this.updatedAt = Instant.now();
    }

}
