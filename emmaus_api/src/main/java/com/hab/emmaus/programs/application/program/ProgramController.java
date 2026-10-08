package com.hab.emmaus.programs.application.program;

import com.hab.emmaus.programs.application.program.dto.CreateProgramRequest;
import com.hab.emmaus.programs.application.program.dto.ProgramResponse;
import com.hab.emmaus.shared.common_utils.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/programs")
@Tag(name = "programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @PostMapping("/create")
    public ResponseEntity<?> createProgram(@RequestBody CreateProgramRequest request){
        programService.createProgram(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{public-id}")
    public ResponseEntity<?> updateProgram(
            @PathVariable("public-id") UUID publicId,
            @RequestBody CreateProgramRequest request
    ){
        programService.updateProgram(publicId,request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{public-id}")
    public ResponseEntity<?> deleteProgram(
            @PathVariable("public-id") UUID publicId
    ){
        programService.deleteProgram(publicId);
        return ResponseEntity.ok().build();
    }

    /** getters **/
    @GetMapping("/pages")
    public ResponseEntity<PageResponse<ProgramResponse>> getPagesOfPrograms(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "public-id", required = false) UUID publicId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ){
        var res = programService.getPagesOfPrograms(name,status,publicId, page,size);
        return ResponseEntity.ok(res);
    }
}
