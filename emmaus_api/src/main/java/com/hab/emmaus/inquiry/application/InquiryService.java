package com.hab.emmaus.inquiry.application;

import com.hab.emmaus.document.application.dto.DocumentResponse;
import com.hab.emmaus.inquiry.application.dto.CreateInquiryRequest;
import com.hab.emmaus.inquiry.application.dto.InquiryResponse;
import com.hab.emmaus.inquiry.application.dto.UpdateInquiryRequest;
import com.hab.emmaus.inquiry.domain.Inquiry;
import com.hab.emmaus.inquiry.persistence.InquiryRepository;
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
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void create(CreateInquiryRequest req) {
        Inquiry inquiry = Inquiry.create(
                req.type(),
                req.senderName(),
                req.senderEmail(),
                req.senderPhone(),
                req.subject(),
                req.message(),
                req.status(),
                req.remark()
        );

        inquiryRepository.save(inquiry);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void update(UUID publicId, UpdateInquiryRequest req){
        Inquiry inquiry = inquiryRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INQUIRY_NOT_FOUND));

        inquiry.update(req.status(), req.remark());
        inquiryRepository.save(inquiry);
    }

    /** getters ************************************************************** **/
    public PageResponse<InquiryResponse> getPagesOfInquiry(
            String type, LocalDate fromDate, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        MapSqlParameterSource params = new MapSqlParameterSource();

        String baseSql = " FROM inquiry i ";

        // 1. Build the Dynamic WHERE clause
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");

        if (type != null && !type.isEmpty()) {
            whereClause.append(" AND LOWER(i.type) = :type");
            params.addValue("type", type.toLowerCase());
        }

        if (fromDate != null) {
            whereClause.append(" AND i.created_at::Date >= :fromDate");
            params.addValue("fromDate", fromDate);
        }

        // 2. Count Query (Must NOT have ORDER BY or LIMIT)
        String countSql = "SELECT COUNT(*) " + baseSql + whereClause;
        Long total = namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);

        if (total == null || total == 0) {
            return PageResponse.toPage(List.of(), 0, pageable);
        }

        // 3. Data Query (Add Order and Pagination)
        String pagination = " ORDER BY i.created_at DESC LIMIT :limit OFFSET :offset";
        params.addValue("limit", size);
        params.addValue("offset", pageable.getOffset());

        String dataSql = """
                SELECT *
                """
                + baseSql + whereClause + pagination;

        List<InquiryResponse> content = namedParameterJdbcTemplate.query(dataSql,params,
                (rs,rowNum) -> InquiryResponse.builder()
                        .publicId(UUID.fromString(rs.getString("public_id")))
                        .type(rs.getString("type"))
                        .subject(rs.getString("subject"))
                        .message(rs.getString("message"))
                        .senderName(rs.getString("sender_name"))
                        .senderPhone(rs.getString("sender_phone"))
                        .remark(rs.getString("remark"))
                        .createdAt(rs.getTimestamp("created_at").toInstant())
                        .status(rs.getString("status"))
                        .build()
        );

        return PageResponse.toPage(content,total,pageable);
    }
}

