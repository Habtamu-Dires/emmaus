package com.hab.emmaus.programs.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Getter
@Table(name = "program")
public class Program {

    @Id
    private Long id;
    private UUID publicId;
    private String name;
    private String description;
    private Integer metricNumber;
    private String metricLabel;
    private String logoUrl;
    private String status;
    private Integer displayOrder;
    private Instant createdAt;
    private Instant updatedAt;

    @PersistenceCreator
    public Program(Long id, UUID publicId, String name, String description, Integer metricNumber, String metricLabel, String logoUrl, String status, Integer displayOrder, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.publicId = publicId;
        this.name = name;
        this.description = description;
        this.metricNumber = metricNumber;
        this.metricLabel = metricLabel;
        this.logoUrl = logoUrl;
        this.status = status;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    private Program(){};

    //create
    public static Program create(String name, String description, Integer metricNumber, String metricLabel, String logoUrl, String status, Integer displayOrder){
        Program program = new Program();
        program.publicId = UUID.randomUUID();
        program.name = name;
        program.description = description;
        program.metricNumber = metricNumber;
        program.metricLabel = metricLabel;
        program.logoUrl = logoUrl;
        program.status = status;
        program.displayOrder = displayOrder;
        program.createdAt = Instant.now();
        program.updatedAt = Instant.now();

        return program;
    }

    //update
    public void update(String name, String description, Integer metricNumber, String metricLabel, String logoUrl, String status, Integer displayOrder){
        this.name = name;
        this.description = description;
        this.metricNumber = metricNumber;
        this.metricLabel = metricLabel;
        this.logoUrl = logoUrl;
        this.status = status;
        this.displayOrder = displayOrder;
        this.updatedAt = Instant.now();
    }
}
