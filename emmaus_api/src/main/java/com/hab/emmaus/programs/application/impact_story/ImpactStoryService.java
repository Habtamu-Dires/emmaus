package com.hab.emmaus.programs.application.impact_story;

import com.hab.emmaus.infrastructure.file_manager.S3Service;
import com.hab.emmaus.programs.application.impact_story.dto.CreateImpactStoryRequest;
import com.hab.emmaus.programs.application.impact_story.dto.ImpactStatisticsResponse;
import com.hab.emmaus.programs.application.impact_story.dto.ImpactStoryResponse;
import com.hab.emmaus.programs.domain.ImpactStory;
import com.hab.emmaus.programs.persistence.ImpactStoryRepository;
import com.hab.emmaus.programs.persistence.ProgramRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImpactStoryService {

    private final ImpactStoryRepository impactStoryRepository;
    private final ProgramRepository programRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final S3Service s3Service;

    @PreAuthorize("hasRole('ADMIN')")
    public void createImpactStory(CreateImpactStoryRequest req) {

        Long programId = programRepository.findIdByPublicId(req.programId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PROGRAM_NOT_FOUND));

        ImpactStory impactStory = ImpactStory.create(
                programId,
                req.beneficiaryName(),
                req.age(),
                req.gender().toString(),
                req.location(),
                req.imageUrl(),
                req.videoUrl(),
                req.shortQuote(),
                req.fullStory(),
                req.status(),
                req.remark(),
                req.displayOrder()
        );

        impactStoryRepository.save(impactStory);

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateImpactStory(UUID publicId, CreateImpactStoryRequest req) {
        ImpactStory impactStory = impactStoryRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STORY_NOT_FOUND));

        impactStory.update(
                req.beneficiaryName(),
                req.age(),
                req.gender().toString(),
                req.location(),
                req.imageUrl(),
                req.videoUrl(),
                req.shortQuote(),
                req.fullStory(),
                req.status(),
                req.remark(),
                req.displayOrder()
        );

        impactStoryRepository.save(impactStory);

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImpactStory(UUID publicId) {
        impactStoryRepository.findByPublicId(publicId)
                .ifPresent((story)->{
                    s3Service.deleteFile(story.getImageUrl());
                    impactStoryRepository.deleteByPublicId(publicId);
                });
    }

    /** getters **/
    public PageResponse<ImpactStoryResponse> getPagesOfImpactStories(
            String name, String gender, UUID publicId, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        MapSqlParameterSource params = new MapSqlParameterSource();

        String baseSql = """
                FROM impact_story s
                JOIN program p ON s.program_id = p.id
                """;

        // 1. Build the Dynamic WHERE clause
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");

        if (name != null && !name.isEmpty()) {
            whereClause.append(" AND LOWER(s.beneficiary_name) LIKE :name");
            params.addValue("name", "%" + name.toLowerCase() + "%");
        }

        if (gender != null && !gender.isEmpty()) {
            whereClause.append(" AND LOWER(s.gender) = :gender");
            params.addValue("gender", gender.toLowerCase());
        }

        if(publicId != null){
            whereClause.append(" AND s.public_id = :publicId");
            params.addValue("publicId", publicId);
        }

        // 2. Count Query (Must NOT have ORDER BY or LIMIT)
        String countSql = "SELECT COUNT(*) " + baseSql + whereClause;
        Long total = namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);

        if (total == null || total == 0) {
            return PageResponse.toPage(List.of(), 0, pageable);
        }

        // 3. Data Query (Add Order and Pagination)
        String pagination = " ORDER BY s.display_order DESC LIMIT :limit OFFSET :offset";
        params.addValue("limit", size);
        params.addValue("offset", pageable.getOffset());

        String dataSql = """
                SELECT 
                 s.public_id,
                 s.beneficiary_name,
                 s.gender,
                 s.age,
                 s.location,
                 s.image_url,
                 s.video_url,
                 s.short_quote,
                 s.full_story,
                 s.status,
                 s.remark,
                 s.created_at,
                 s.updated_at,
                 p.public_id as program_id,
                 p.name as program
                """
                + baseSql + whereClause + pagination;

        List<ImpactStoryResponse> content = namedParameterJdbcTemplate.query(dataSql,params,
                (rs,rowNum) -> ImpactStoryResponse.builder()
                        .publicId(UUID.fromString(rs.getString("public_id")))
                        .programId(UUID.fromString(rs.getString("program_id")))
                        .program(rs.getString("program"))
                        .beneficiaryName(rs.getString("beneficiary_name"))
                        .age(rs.getInt("age"))
                        .gender(rs.getString("gender"))
                        .location(rs.getString("location"))
                        .imageUrl(rs.getString("image_url"))
                        .videoUrl(rs.getString("video_url"))
                        .shortQuote(rs.getString("short_quote"))
                        .fullStory(rs.getString("full_story"))
                        .status(rs.getString("status"))
                        .remark(rs.getString("remark"))
                        .build()
        );

        return PageResponse.toPage(content,total,pageable);
    }

    // impact story metrics
    public ImpactStatisticsResponse getImpactStatistics(Integer year, Integer lastDays) {

        MapSqlParameterSource params = new MapSqlParameterSource();

        String baseSql = """
                FROM impact_story s
                JOIN program p ON s.program_id = p.id
                """;

        // 1. Build the Dynamic WHERE clause
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");

        if (year != null) {
            whereClause.append(" AND updated_at::Date::Year = '");
            params.addValue("year", year);
        }

        if (lastDays != null) {
            LocalDate lastDate = LocalDate.now().minusDays(lastDays);
            whereClause.append(" AND updated_at::Date >= :lastDate");
            params.addValue("lastDate", lastDate);
        }

        String dataSql = """
                SELECT 
                 (COUNT(s.id)) AS total_impacts,
                 (COUNT(CASE WHEN s.age < 12 THEN 1 END)) AS child_impacts,
                 (COUNT(CASE WHEN s.age > 60 THEN 1 END)) AS elderly_impacts,
                 (COUNT(CASE WHEN s.gender = 'FEMALE' THEN 1 END)) AS women_impacts,
                 (COUNT(CASE WHEN LOWER(p.name) = 'special needs' THEN 1 END)) AS special_needs_impacts
                """
                + baseSql + whereClause;

        return namedParameterJdbcTemplate.queryForObject(dataSql,params,
                (rs,rowNum) -> ImpactStatisticsResponse.builder()
                        .totalImpacts(rs.getInt("total_impacts"))
                        .childImpacts(rs.getInt("child_impacts"))
                        .womenImpacts(rs.getInt("women_impacts"))
                        .elderlyImpacts(rs.getInt("elderly_impacts"))
                        .specialNeedsImpacts(rs.getInt("special_needs_impacts"))
                        .build()
        );

    }
}
