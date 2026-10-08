package com.hab.emmaus.document.persistence;

import com.hab.emmaus.document.domain.Document;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends CrudRepository<Document,Long> {

    @Query("""
      SELECT * FROM document 
      WHERE public_id = :publicId
    """)
    Optional<Document> findByPublicId(UUID publicId);

    @Modifying
    @Query("""
            DELETE FROM document WHERE public_id = :publicId
            """)
    void deleteByPublicId(UUID publicId);
}
