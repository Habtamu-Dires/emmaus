package com.hab.emmaus.staff_profile.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Table("staff_profile")
public class StaffProfile {

    @Id
    private Long id;
    private UUID publicId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String position;
    private String department;
    private String description;
    private String bio;
    private String profilePic;
    private String linkedinUrl;
    private Integer displayOrder;
    private Instant createdAt = Instant.now();

    @PersistenceCreator
    public StaffProfile(Long id, UUID publicId, String firstName, String lastName, String email, String phone, String position, String department, String description, String bio, String profilePic, String linkedinUrl, Integer displayOrder, Instant createdAt ){
        this.id = id;
        this.publicId = publicId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.department = department;
        this.description = description;
        this.bio = bio;
        this.profilePic = profilePic;
        this.linkedinUrl = linkedinUrl;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }
    private StaffProfile(){};

    //behaviour
    //create
    public static StaffProfile create(String firstName, String lastName, String email, String phone, String position, String department, String description, String bio, String profilePic, String linkedinUrl, Integer displayOrder){
        StaffProfile profile = new StaffProfile();
        profile.publicId = UUID.randomUUID();
        profile.firstName = firstName;
        profile.lastName = lastName;
        profile.email = email;
        profile.phone = phone;
        profile.position = position;
        profile.department = department;
        profile.description = description;
        profile.bio = bio;
        profile.profilePic = profilePic;
        profile.linkedinUrl = linkedinUrl;
        profile.displayOrder = displayOrder;

        return profile;
    }

    public  void update(String firstName, String lastName, String email, String phone, String position, String department, String description, String bio, String profilePic, String linkedinUrl, Integer displayOrder){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.position = position;
        this.department = department;
        this.description = description;
        this.bio = bio;
        this.profilePic = profilePic;
        this.linkedinUrl = linkedinUrl;
        this.displayOrder = displayOrder;
    }
}


