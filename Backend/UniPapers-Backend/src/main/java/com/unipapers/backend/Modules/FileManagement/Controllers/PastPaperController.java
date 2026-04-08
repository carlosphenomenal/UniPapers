import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pastpapers")
@RequiredArgsConstructor

public class PastPaperController {
    private final PastPaperService pastPaperService;

    @GetMapping
    public ResponseEntity<List<PastPaper>> getAllPastPapers() {
        return ResponseEntity.ok(pastPaperService.getAllPastPapers());
    }

    @PostMapping
    public ResponseEntity<PastPaper> createPastPaper(@RequestBody PastPaper pastPaper)
    }

}