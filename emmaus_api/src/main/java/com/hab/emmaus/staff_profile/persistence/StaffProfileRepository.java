package com.hab.emmaus.staff_profile.persistence;

import com.hab.emmaus.staff_profile.domain.StaffProfile;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface StaffProfileRepository extends CrudRepository<StaffProfile, Long> {

    @Query("""
            SELECT * FROM staff_profile 
            WHERE phone = :phone
             OR email = :email
            """)
    Optional<StaffProfile> findByPhoneOrEmail(String phone, String email);

    @Query("""
             SELECT * FROM staff_profile 
            WHERE public_id = :publicId
            """)
    Optional<StaffProfile> findByPublicId(UUID publicId);

    @Query("""
            SELECT * FROM staff_profile 
            WHERE email = :email
            """)
    Optional<StaffProfile> findByEmail(String email);

    @Query("""
            SELECT * FROM staff_profile 
            WHERE phone = :phone
            """)
    Optional<StaffProfile>  findByPhone(String phone);

    @Modifying
    @Query("""
            DELETE FROM staff_profile 
            WHERE public_id = :publicId
            """)
    void deleteByPublicId(UUID publicId);
}
