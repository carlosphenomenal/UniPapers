package com.unipapers.backend.Modules.Auth.Services;

import com.unipapers.backend.Common.Models.User;
import com.unipapers.backend.Common.Repositories.UserRepo;
import com.unipapers.backend.Utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    @NullMarked
    public CustomUserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        long studentNum;

        // Check if the input is numeric
        try {
            studentNum = Long.parseLong(identifier);
        } catch (NumberFormatException e) {
            // Not a number, so we know it can't be a student number
            // We set it to a value that won't exist in the DB
            studentNum = -1L;
        }

        // Pass the original string as username AND the parsed/dummy number
        User user = userRepo.findByEmailOrStudentNumber(identifier, studentNum)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with identifier: " + identifier));

        List<String> roles = user.getRoles() == null
                ? List.of()
                : user.getRoles().stream().map(Enum::name).toList();

        return new CustomUserDetails(
                user.getId(),
                user.getPublicId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPassword(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getStudentNumber() != null ? user.getStudentNumber() : 0L,
                user.getYearOfStudy() != null ? user.getYearOfStudy() : 0,
                user.getSemester() != null ? user.getSemester() : 0,
                roles
        );
    }
}
