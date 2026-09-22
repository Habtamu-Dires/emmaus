package com.hab.emmaus.app_user.application.app_user;


import com.hab.emmaus.app_user.application.dto.TextDto;
import com.hab.emmaus.app_user.application.app_user.dto.CreateUserRequest;
import com.hab.emmaus.app_user.application.app_user.dto.UserProfile;
import com.hab.emmaus.app_user.application.app_user.dto.UserResponse;
import com.hab.emmaus.app_user.application.reset_token.ResetService;
import com.hab.emmaus.app_user.domain.app_user.AppUser;
import com.hab.emmaus.app_user.domain.app_user.enums.UserStatus;
import com.hab.emmaus.app_user.event.AdminSetPasswordEvent;
import com.hab.emmaus.app_user.persistence.AppUserRepository;
import com.hab.emmaus.infrastructure.security.SecurityUtils;
import com.hab.emmaus.shared.common_utils.PageResponse;
import com.hab.emmaus.shared.exception.BusinessException;
import com.hab.emmaus.shared.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ResetService resetService;


//    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public TextDto createUser(CreateUserRequest req){
            checkUserEmail(req.email());
            String password = generateRandomPassword(8);
            String passwordHash = passwordEncoder.encode(password);

        AppUser user = AppUser.register(
                req.firstName(),
                req.lastName(),
                req.email(),
                Set.of(req.role()),
                passwordHash,
                req.remark()
        );
        user.verifyEmail();
        user.setPasswordAsTemp();
        user.activate();
        AppUser savedUser = userRepo.save(user);

        // send password
        publisher.publishEvent(
            AdminSetPasswordEvent.builder()
                    .userName(req.firstName() + " " + req.lastName())
                    .email(req.email())
                    .password(password)
                    .reason("New User")
                    .build()
        );

        return new TextDto(savedUser.getPublicId().toString());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void addRemark(UUID publicId, String remark){
        AppUser user = userRepo.findByPublicId(publicId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.addRemark(remark);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserProfile getUserByPhone(String phone){
        AppUser userIdentity = userRepo.findByEmail(phone)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserProfile.from(userIdentity);
    }

    public UserProfile getUserProfile(){
        UUID userId = SecurityUtils.currentUser().userPublicId();
        AppUser user = userRepo.findByPublicId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserProfile.from(user);
    }

    public UserProfile updateProfile(UserProfile profile){
        UUID loggedId = SecurityUtils.currentUser().userPublicId();
        if(!loggedId.equals(profile.publicId())){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED_IN_CURRENT_STATE);
        }

        AppUser user = userRepo.findByPublicId(loggedId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.update(
                profile.profilePic(),
                profile.firstName(),
                profile.lastName()
        );

        userRepo.save(user);


        return UserProfile.from(user);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void activate(UUID publicId) {
        AppUser user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.activate();
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public void disable(UUID publicId, String remark) {
        AppUser user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.disable(remark);
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void verifyPhone(UUID publicId) {
        AppUser user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.verifyEmail();
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void reject(UUID publicId) {
        AppUser user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.reject("Rejected by Admin");
        userRepo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void  deleteUserIdentity(UUID publicId) {
        AppUser user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userRepo.delete(user);
    }

    /** getters ************************************************************** **/

//    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public PageResponse<UserProfile> getPagesOfUser(
            UserStatus status, String email, String name, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        MapSqlParameterSource params = new MapSqlParameterSource();

        String baseSql = "FROM user_identity u";

        // 1. Build the Dynamic WHERE clause
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");
//        whereClause.append(" AND ('ADMIN' = ANY(roles) OR 'SUPER_ADMIN' = ANY(roles))");

        if (status != null) {
            whereClause.append(" AND u.status = :status ");
            params.addValue("status", status.name() );
        }
        if (email != null && !email.isEmpty()) {
            whereClause.append(" AND LOWER(u.email) = :email");
            params.addValue("email", email.toLowerCase());
        }

        if (name != null && !name.isEmpty()) {
            whereClause.append(" AND LOWER(u.first_name) LIKE :name");
            params.addValue("name", "%" + name.toLowerCase() + "%");
        }


        // 2. Count Query (Must NOT have ORDER BY or LIMIT)
        String countSql = "SELECT COUNT(*) " + baseSql + whereClause;
        Long total = namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);

        if (total == null || total == 0) {
            return PageResponse.toPage(List.of(), 0, pageable);
        }

        // 3. Data Query (Add Order and Pagination)
        String pagination = " ORDER BY u.created_at DESC LIMIT :limit OFFSET :offset";
        params.addValue("limit", size);
        params.addValue("offset", pageable.getOffset());

        String dataSql = "SELECT * " + baseSql + whereClause + pagination;

        List<UserProfile> content = namedParameterJdbcTemplate.query(dataSql,params,
                (rs,rowNum) -> UserProfile.builder()
                        .publicId(UUID.fromString(rs.getString("public_id")))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .email(rs.getString("email"))
                        .profilePic(rs.getString("profile_pic"))
                        .status(UserStatus.valueOf(rs.getString("status")))
                        .remark(rs.getString("remark"))
                        .createdAt(rs.getTimestamp("created_at").toInstant())
                        .updatedAt(rs.getTimestamp("updated_at").toInstant())
                        .isEmailVerified(rs.getBoolean("is_email_verified"))
                        .build());

        return PageResponse.toPage(content, total,pageable);
    }

    // filter users
    public PageResponse<UserResponse> filterUsers(UUID branchId, String name, String phone, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        MapSqlParameterSource params = new MapSqlParameterSource();

        String baseSql = """
                FROM user_identity u 
                JOIN user_assignment ua ON u.id = ua.user_id
                JOIN user_role ur ON ur.id = ua.role_id
                JOIN branch b ON b.id = ua.branch_id
                """;

        // 1. Build the Dynamic WHERE clause
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");

        if(branchId != null){
            whereClause.append(" AND b.public_id = :branchPublicId");
            params.addValue("branchPublicId", branchId);
        }

        if (phone != null && !phone.isEmpty()) {
            whereClause.append(" AND LOWER(u.email) LIKE :email");
            params.addValue("email", "%" + phone.toLowerCase() + "%");
        }

        if (name != null && !name.isEmpty()) {
            whereClause.append(" AND LOWER(u.first_name) LIKE :name");
            params.addValue("name", "%" + name.toLowerCase() + "%");
        }


        // 2. Count Query (Must NOT have ORDER BY or LIMIT)
        String countSql = "SELECT COUNT(*) " + baseSql + whereClause;
        Long total = namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);

        if (total == null || total == 0) {
            return PageResponse.toPage(List.of(), 0, pageable);
        }

        // 3. Data Query (Add Order and Pagination)
        String pagination = " ORDER BY u.created_at DESC LIMIT :limit OFFSET :offset";
        params.addValue("limit", size);
        params.addValue("offset", pageable.getOffset());

        String dataSql = """
                SELECT 
                    u.public_id, u.first_name, u.last_name, u.email, ur.name , ua.discipline
                """
                + baseSql + whereClause + pagination;

        List<UserResponse> content = namedParameterJdbcTemplate.query(dataSql,params,
                (rs,rowNum) -> UserResponse.builder()
                        .publicId(UUID.fromString(rs.getString("public_id")))
                        .name(rs.getString("first_name") + " "
                                + rs.getString("last_name"))
                        .phone(rs.getString("email"))
                        .role(rs.getString("name"))
                        .discipline(rs.getString("discipline"))
                        .build()
        );

        return PageResponse.toPage(content,total,pageable);

    }


    /** helper methods **/
    private void checkPasswords(final String password,
                                final String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    private void checkUserEmail(final String email) {
        this.userRepo.findByEmail(email)
                .ifPresent(userIdentity -> {
                    throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
                });
    }

    // generate password
    private String generateRandomPassword(int length) {
        if (length < 4) throw new IllegalArgumentException("Length must be at least 4");

         String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
         String LOWER = "abcdefghijklmnopqrstuvwxyz";
         String DIGITS = "0123456789";
         String SPECIAL = "!@#$%^&*()-_=+[]{}";
         String ALL_CHARS = UPPER + LOWER + DIGITS + SPECIAL;


        // SecureRandom is the standard for cryptographic security
        RandomGenerator random = new SecureRandom();

        // 1. Ensure at least one of each required type
        StringBuilder password = new StringBuilder();
        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // 2. Fill the rest of the length with random characters from the full pool
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // 3. Shuffle so the required characters aren't always at the start
        List<Character> letters = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(letters);

        return letters.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

}
