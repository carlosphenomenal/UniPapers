package com.unipapers.backend.Modules.FileManagement.Repositories;

import com.unipapers.backend.Modules.FileManagement.Models.PastPaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PastPaperRepo extends JpaRepository<PastPaper, Long> {
}
