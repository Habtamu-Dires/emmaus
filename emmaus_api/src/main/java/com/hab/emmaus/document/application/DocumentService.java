package com.hab.emmaus.document.application;

import com.hab.emmaus.document.application.dto.CreateDocumentRequest;
import com.hab.emmaus.document.application.dto.DocumentResponse;
import com.hab.emmaus.document.domain.Document;
import com.hab.emmaus.document.persistence.DocumentRepository;
import com.hab.emmaus.infrastructure.file_manager.S3Service;
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
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final S3Service s3Service;


    //create
    @PreAuthorize("hasRole('ADMIN')")
    public void create(CreateDocumentRequest req){
        Document document = Document.create(
                req.documentType(),
                req.title(),
                req.description(),
                req.url(),
                req.thumbnailUrl(),
                req.publishedDate(),
                req.displayOrder()
        );
        documentRepository.save(document);
    }

    // update
    @PreAuthorize("hasRole('ADMIN')")
    public void update(UUID publicId, CreateDocumentRequest req){
        Document document = documentRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND));

        document.update(
                req.documentType(),
                req.title(),
                req.description(),
                req.url(),
                req.thumbnailUrl(),
                req.publishedDate(),
                req.displayOrder()
        );
        documentRepository.save(document);
    }

    //delete
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(UUID publicId){
        documentRepository.findByPublicId(publicId)
                        .ifPresent(document -> {
                            s3Service.deleteFile(document.getUrl());
                            s3Service.deleteFile(document.getThumbnailUrl());
                            documentRepository.deleteByPublicId(publicId);
                        });

    }

    /** getters **/

    public PageResponse<DocumentResponse> getDocuments(
             String docType, String title, int page, int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        MapSqlParameterSource params = new MapSqlParameterSource();

        String baseSql = " FROM document d ";

        // 1. Build the Dynamic WHERE clause
        StringBuilder whereClause = new StringBuilder(" WHERE 1=1 ");

        if (docType != null && !docType.isEmpty()) {
            whereClause.append(" AND LOWER(d.document_type) = :docType");
            params.addValue("docType", docType.toLowerCase());
        }

        if (title != null && !title.isEmpty()) {
            whereClause.append(" AND LOWER(d.title) LIKE :title");
            params.addValue("title", "%" + title.toLowerCase() + "%");
        }

        // 2. Count Query (Must NOT have ORDER BY or LIMIT)
        String countSql = "SELECT COUNT(*) " + baseSql + whereClause;
        Long total = namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);

        if (total == null || total == 0) {
            return PageResponse.toPage(List.of(), 0, pageable);
        }

        // 3. Data Query (Add Order and Pagination)
        String pagination = " ORDER BY d.display_order DESC LIMIT :limit OFFSET :offset";
        params.addValue("limit", size);
        params.addValue("offset", pageable.getOffset());

        String dataSql = """
                SELECT *
                """
                + baseSql + whereClause + pagination;

        List<DocumentResponse> content = namedParameterJdbcTemplate.query(dataSql,params,
                (rs,rowNum) -> DocumentResponse.builder()
                        .publicId(UUID.fromString(rs.getString("public_id")))
                        .documentType(rs.getString("document_type"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .url(rs.getString("url"))
                        .thumbnailUrl(rs.getString("thumbnail_url"))
                        .publishedDate(rs.getDate("publication_date").toLocalDate())
                        .displayOrder(rs.getInt("display_order"))
                        .uploadedAt(rs.getTimestamp("uploaded_at").toInstant())
                        .build()
        );

        return PageResponse.toPage(content,total,pageable);
    }
}
