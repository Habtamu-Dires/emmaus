package com.hab.emmaus.inquiry.persistence;

import com.hab.emmaus.inquiry.domain.Inquiry;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface InquiryRepository extends CrudRepository<Inquiry, Long> {

    @Query("""
            SELECT * FROM inquiry
            WHERE public_id = :publicId
            """)
    Optional<Inquiry> findByPublicId(UUID publicId);
}
