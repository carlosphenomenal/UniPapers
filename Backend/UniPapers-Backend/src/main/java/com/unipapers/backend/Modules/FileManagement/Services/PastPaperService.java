import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor

public class PastPaperService {
    private final PastPaperRepo pastPaperRepo;

    public List<PastPaper> getAllPastPapers() {
        return pastPaperRepo.findAll();
    }
}