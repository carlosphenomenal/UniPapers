package com.unipapers.backend.Modules.Profile.Services;

import com.unipapers.backend.Common.Repositories.UserRepo;
import com.unipapers.backend.Modules.Profile.Dtos.ProfileResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepo userRepo;

    @Transactional
    // TODO: Add caching to this method to improve performance, since profile data doesn't change frequently
    public ProfileResponseDto getProfile(String publicId) {
        var user = userRepo.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with publicId: " + publicId));

        String fullName = ((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                + (user.getLastName() == null ? "" : user.getLastName())).trim();

        return ProfileResponseDto.builder()
                .fullName(fullName.trim())
                .email(user.getEmail())
                .studentNumber(user.getStudentNumber() != null ? String.valueOf(user.getStudentNumber()) : null)
                .programme(user.getProgram() != null ? user.getProgram().getProgramName() : null)
                .yearOfStudy(user.getYearOfStudy())
                .semester(user.getSemester())
                .uploadedPastPapersCount(user.getPastPapers() != null ? user.getPastPapers().size() : 0)
                .build();
    }

}
