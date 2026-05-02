package com.unipapers.backend.Modules.Program.Controllers;

import com.unipapers.backend.Modules.Program.Dtos.ProgramResponseDto;
import com.unipapers.backend.Modules.Program.Services.ProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/programs")
@RequiredArgsConstructor
public class ProgramController {

    private final ProgramService programService;

    @GetMapping("/get")
    public ResponseEntity<List<ProgramResponseDto>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }
}

