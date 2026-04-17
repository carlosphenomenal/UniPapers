package com.unipapers.backend.Modules.FileManagement.Controllers;

import com.unipapers.backend.Modules.FileManagement.Dtos.PastPaperResponseDto;
import com.unipapers.backend.Modules.FileManagement.Services.PastPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/pastpapers", "/past-papers"})
@RequiredArgsConstructor
public class PastPaperController {
    private final PastPaperService pastPaperService;

    // TODO: Add userId from @AuthenticationPrincipal for more precise results
    @GetMapping("/get")
    public ResponseEntity<List<PastPaperResponseDto>> getAllPastPapers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String filter
    ) {
        return ResponseEntity.ok(pastPaperService.getAllPastPapers(query, filter));
    }

}