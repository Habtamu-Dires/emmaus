package com.hab.emmaus.programs.persistence;

import com.hab.emmaus.programs.domain.ImpactStory;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ImpactStoryRepository extends CrudRepository<ImpactStory, Long> {

    @Query("""
            SELECT * FROM impact_story
            WHERE public_id = :publicId
            """)
    Optional<ImpactStory> findByPublicId(UUID publicId);

    @Modifying
    @Query("""
            DELETE FROM impact_story
            WHERE public_id = :publicId
            """)
    void deleteByPublicId(UUID publicId);
}
