package com.hab.emmaus.programs.application.impact_story;

import com.hab.emmaus.programs.application.impact_story.dto.CreateImpactStoryRequest;
import com.hab.emmaus.programs.application.impact_story.dto.ImpactStatisticsResponse;
import com.hab.emmaus.programs.application.impact_story.dto.ImpactStoryResponse;
import com.hab.emmaus.shared.common_utils.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/impact-stories")
@Tag(name = "impact-stories")
@RequiredArgsConstructor
public class ImpactStoryController {

    private final ImpactStoryService impactStoryService;

    @PostMapping("/create")
    public ResponseEntity<?> createImpactStory(
            @RequestBody CreateImpactStoryRequest req
    ){
        impactStoryService.createImpactStory(req);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{public-id}")
    public ResponseEntity<?> updateImpactStory(
            @PathVariable("public-id") UUID publicId,
            @RequestBody CreateImpactStoryRequest req
    ){
        impactStoryService.updateImpactStory(publicId,req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{public-id}")
    public ResponseEntity<?> deleteImpactStory(
            @PathVariable("public-id") UUID publicId
    ){
        impactStoryService.deleteImpactStory(publicId);
        return ResponseEntity.ok().build();
    }

    /** getters **/
    @GetMapping("/pages")
    public ResponseEntity<PageResponse<ImpactStoryResponse>> getPagesOfImpactStories(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "gender", required = false) String gender,
            @RequestParam(name = "public-id", required = false) UUID publicId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ){
        var res = impactStoryService.getPagesOfImpactStories(name,gender,publicId,page, size);
        return ResponseEntity.ok(res);
    }

    // get impact stores metircs
    @GetMapping("/statistics")
    public ResponseEntity<ImpactStatisticsResponse> getImpactStatistics(
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "last-days", required = false) Integer lastDays
    ){
        var res = impactStoryService.getImpactStatistics(year, lastDays);
        return ResponseEntity.ok(res);
    }
}
