package com.hab.emmaus.programs.application.program;

import com.hab.emmaus.infrastructure.file_manager.S3Service;
import com.hab.emmaus.programs.application.program.dto.CreateProgramRequest;
import com.hab.emmaus.programs.application.program.dto.ProgramResponse;
import com.hab.emmaus.programs.domain.Program;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final S3Service s3Service;

    @PreAuthorize("hasRole('ADMIN')")
    public void createProgram(CreateProgramRequest req) {
        Program program = Program.create(
                req.name(),
                req.description(),
                req.metricNumber(),
                req.metricLabel(),
                req.logoUrl(),
                req.status(),
                req.displayOrder()
        );

        programRepository.save(program);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateProgram(UUID publicId, CreateProgramRequest req) {
        Program program = programRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROGRAM_NOT_FOUND));

        program.update(
                req.name(),
                req.description(),
                req.metricNumber(),
                req.metricLabel(),
                req.logoUrl(),
                req.status(),
                req.displayOrder()
        );

        programRepository.save(program);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProgram(UUID publicId) {
        programRepository.findByPublicId(publicId)
                .ifPresent(program -> {
                    s3Service.deleteFile(program.getLogoUrl());
                    programRepository.deleteByPublicId(publicId);
                });
    }

    /** getters **/
    public PageResponse<ProgramResponse> getPagesOfPrograms(
            String name, String status,UUID publicId, int page, int size
    ){

        Pageable pageable = PageRequest.of(page, size);
        MapSqlParameterSource params = new MapSqlParameterSource();

        String baseSql = " FROM program p ";

        // 1. Build the Dynamic WHERE clause
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");

        if (name != null && !name.isEmpty()) {
            whereClause.append(" AND LOWER(p.name) LIKE :name");
            params.addValue("name", "%" + name.toLowerCase() + "%");
        }

        if (status != null && !status.isEmpty()) {
            whereClause.append(" AND LOWER(p.status) = :status");
            params.addValue("status", status.toLowerCase());
        }

        if(publicId != null){
            whereClause.append(" AND p.public_id = :publicId");
            params.addValue("publicId", publicId);
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

        List<ProgramResponse> content = namedParameterJdbcTemplate.query(dataSql,params,
                (rs,rowNum) -> ProgramResponse.builder()
                        .publicId(UUID.fromString(rs.getString("public_id")))
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .metricNumber(rs.getInt("metric_number"))
                        .metricLabel(rs.getString("metric_label"))
                        .logoUrl(rs.getString("logo_url"))
                        .status(rs.getString("status"))
                        .displayOrder(rs.getInt("display_order"))
                        .build()
        );

        return PageResponse.toPage(content,total,pageable);
    }


}
