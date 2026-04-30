package com.unipapers.backend.Modules.Auth.Services;

import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Common.Repositories.UserRepo;
import com.unipapers.backend.Modules.FileManagement.Models.Program;
import com.unipapers.backend.Modules.FileManagement.Repositories.ProgramRepo;
import com.unipapers.backend.Modules.Auth.Dtos.SignupRequestDto;
import com.unipapers.backend.Utils.SemesterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final EmailService emailService;
    private final UserRepo userRepo;
    private final ProgramRepo programRepo;
    private final PasswordEncoder passwordEncoder;

    public Object signup(SignupRequestDto signupRequestDto) {
        // Check if a user with the same student number or email exists
        if (userRepo.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists with email: " + signupRequestDto.getEmail());
        }
        if (userRepo.findByStudentNumber(signupRequestDto.getStudentNumber()).isPresent()) {
            throw new IllegalArgumentException("User already exists with student number: " + signupRequestDto.getStudentNumber());
        }

        Program program = programRepo.findByPublicId(signupRequestDto.getProgrammePublicId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Program not found with publicId: " + signupRequestDto.getProgrammePublicId()
                ));

        // Register the user in the db
        User user = User.builder()
                .firstName(signupRequestDto.getFirstName())
                .lastName(signupRequestDto.getLastName())
                .email(signupRequestDto.getEmail())
                .studentNumber(signupRequestDto.getStudentNumber())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .program(program)
                .yearOfStudy(signupRequestDto.getYearOfStudy())
                .semester(SemesterUtils.currentSemester())
                .build();

        userRepo.save(user);

        // Generate a random 6-digit code (simple implementation for now)
        String verificationCode = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Send a code to the email
        emailService.sendVerificationCode(signupRequestDto.getEmail(), verificationCode);

        return "Verification code sent to " + signupRequestDto.getEmail();
    }
}
