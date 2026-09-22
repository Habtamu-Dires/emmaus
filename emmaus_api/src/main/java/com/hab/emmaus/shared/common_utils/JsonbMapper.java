package com.hab.emmaus.shared.common_utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class JsonbMapper {

//    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ObjectMapper objectMapper =
            JsonMapper.builder()
                    .addModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .build();

    public <T> T readObject(
            ResultSet rs,
            String column,
            Class<T> targetType
    ) throws SQLException {

        PGobject pgObject = rs.getObject(column, PGobject.class);

        if (pgObject == null || pgObject.getValue() == null) {
            return null;
        }

        try {
            // Read directly into the target wrapper class
            return objectMapper.readValue(pgObject.getValue(), targetType);

        } catch (JsonProcessingException e) {
            throw new SQLException(
                    "Failed to deserialize JSONB column: " + column,
                    e
            );
        }
    }
}