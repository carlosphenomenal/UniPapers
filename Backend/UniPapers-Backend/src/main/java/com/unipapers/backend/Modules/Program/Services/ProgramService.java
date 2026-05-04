package com.unipapers.backend.Modules.Program.Services;

import com.unipapers.backend.Modules.Program.Dtos.ProgramResponseDto;
import com.unipapers.backend.Modules.Program.Models.Program;
import com.unipapers.backend.Modules.Program.Repositories.ProgramRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepo programRepo;

    public List<ProgramResponseDto> getAllPrograms() {
        return programRepo.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private ProgramResponseDto mapToResponseDto(Program program) {
        return ProgramResponseDto.builder()
                .publicId(program.getPublicId())
                .programCode(program.getProgramCode())
                .programName(program.getProgramName())
                .durationYears(program.getDurationYears())
                .build();
    }
}

