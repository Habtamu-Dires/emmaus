package com.hab.emmaus.programs.persistence;

import com.hab.emmaus.programs.domain.Program;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProgramRepository extends CrudRepository<Program, Long> {

    @Query("""
            SELECT * FROM program
            WHERE public_id = :publicId
            """)
    Optional<Program> findByPublicId(UUID publicId);

    @Query("""
            SELECT id FROM program 
            WHERE public_id = :publicId
            """)
    Optional<Long> findIdByPublicId(UUID publicId);

    @Modifying
    @Query("""
            DELETE FROM program
            WHERE public_id = :publicId
            """)
    void deleteByPublicId(UUID publicId);
}
