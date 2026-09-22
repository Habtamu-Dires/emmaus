package com.hab.emmaus.app_user.persistence;

import com.hab.emmaus.app_user.domain.app_user.AppUser;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends CrudRepository<AppUser, Long> {

    @Query("""
            SELECT * FROM app_user
            WHERE public_id = :publicId
            """)
    Optional<AppUser> findByPublicId(UUID publicId);
    

    @Query("""
            SELECT * FROM app_user 
            WHERE email = :email
            LIMIT 1
            """)
    Optional<AppUser> findByEmail(String email);


}
