package com.unipapers.backend.Modules.Program.Repositories;

import com.unipapers.backend.Modules.Program.Models.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramRepo extends JpaRepository<Program, Long> {
    Optional<Program> findByPublicId(String publicId);
}
