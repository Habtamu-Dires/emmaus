package com.hab.emmaus.staff_profile.application;

import com.hab.emmaus.infrastructure.file_manager.S3Service;
import com.hab.emmaus.staff_profile.application.dto.CreatStaffProfileRequest;
import com.hab.emmaus.staff_profile.application.dto.StaffProfileResponse;
import com.hab.emmaus.staff_profile.domain.StaffProfile;
import com.hab.emmaus.staff_profile.persistence.StaffProfileRepository;
import com.hab.emmaus.shared.common_utils.PageResponse;
import com.hab.emmaus.shared.exception.BusinessException;
import com.hab.emmaus.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffProfileService {

    private final StaffProfileRepository profileRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final S3Service s3Service;

    //create leader profile
    @PreAuthorize("hasRole('ADMIN')")
    public void create(CreatStaffProfileRequest req){
       profileRepository.findByPhoneOrEmail(req.phone(),req.email())
               .ifPresent(profile -> {
                   throw new BusinessException(ErrorCode.EMAIL_PHONE_ALREADY_EXISTS);
               });

        StaffProfile profile = StaffProfile.create(
                req.firstName(),
                req.lastName(),
                req.email(),
                req.phone(),
                req.position(),
                req.department(),
                req.description(),
                req.bio(),
                req.profilePic(),
                req.linkedinUrl(),
                req.displayOrder()
        );

        profileRepository.save(profile);
    }

    //update leader profile
    @PreAuthorize("hasRole('ADMIN')")
    public void update(UUID publicId, CreatStaffProfileRequest req){
        StaffProfile leader = profileRepository.findByPublicId(publicId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        //check email duplication
        if(!leader.getEmail().equals(req.email())){
            profileRepository.findByEmail(req.email())
                    .ifPresent(profile -> {
                        throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
                    });
        }
        //check if phone duplication
        if(!leader.getPhone().equals(req.phone())){
            profileRepository.findByPhone(req.phone())
                    .ifPresent(profile -> {
                        throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
                    });
        }

       leader.update(
               req.firstName(),
               req.lastName(),
               req.email(),
               req.phone(),
               req.position(),
               req.department(),
               req.description(),
               req.bio(),
               req.profilePic(),
               req.linkedinUrl(),
               req.displayOrder()
       );

        profileRepository.save(leader);
    }

    //delete
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID publicId){
        profileRepository.findByPublicId(publicId)
                .ifPresent(profile -> {
                    s3Service.deleteFile(profile.getProfilePic());
                    profileRepository.deleteByPublicId(publicId);
                });

    }

    /** getters ***/
    public PageResponse<StaffProfileResponse> getStaffProfiles(
           String name, String position, String email, String phone, String department, int page, int size
    ){

        Pageable pageable = PageRequest.of(page, size);
        MapSqlParameterSource params = new MapSqlParameterSource();

        String baseSql = " FROM staff_profile p ";

        // 1. Build the Dynamic WHERE clause
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");

        if (name != null && !name.isEmpty()) {
            whereClause.append(" AND LOWER(p.first_name) LIKE :name");
            params.addValue("name", "%" + name.toLowerCase() + "%");
        }

        if (phone != null && !phone.isEmpty()) {
            whereClause.append(" AND LOWER(p.phone) = :phone");
            params.addValue("phone", phone.toLowerCase());
        }

        if (email != null && !email.isEmpty()) {
            whereClause.append(" AND LOWER(p.email) = :email");
            params.addValue("email", email.toLowerCase());
        }

        if (position != null && !position.isEmpty()) {
            whereClause.append(" AND LOWER(p.position) = :position");
            params.addValue("position", position.toLowerCase() );
        }

        if (department != null && !department.isEmpty()) {
            whereClause.append(" AND LOWER(p.department) = :department");
            params.addValue("department", department.toLowerCase());
        }


        // 2. Count Query (Must NOT have ORDER BY or LIMIT)
        String countSql = "SELECT COUNT(*) " + baseSql + whereClause;
        Long total = namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);

        if (total == null || total == 0) {
            return PageResponse.toPage(List.of(), 0, pageable);
        }

        // 3. Data Query (Add Order and Pagination)
        String pagination = " ORDER BY p.display_order DESC LIMIT :limit OFFSET :offset";
        params.addValue("limit", size);
        params.addValue("offset", pageable.getOffset());

        String dataSql = """
                SELECT *
                """
                + baseSql + whereClause + pagination;

        List<StaffProfileResponse> content = namedParameterJdbcTemplate.query(dataSql,params,
                (rs,rowNum) -> StaffProfileResponse.builder()
                        .publicId(UUID.fromString(rs.getString("public_id")))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .phone(rs.getString("phone"))
                        .email(rs.getString("email"))
                        .position(rs.getString("position"))
                        .department(rs.getString("department"))
                        .bio(rs.getString("bio"))
                        .description(rs.getString("description"))
                        .profilePic(rs.getString("profile_pic"))
                        .linkedinUrl(rs.getString("linkedin_url"))
                        .displayOrder(rs.getInt("display_order"))
                        .createdAt(rs.getTimestamp("created_at").toInstant())
                        .build()
        );

        return PageResponse.toPage(content,total,pageable);

    }
}
