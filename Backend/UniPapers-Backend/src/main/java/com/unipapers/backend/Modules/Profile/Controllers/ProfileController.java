package com.unipapers.backend.Modules.Profile.Controllers;

import com.unipapers.backend.Modules.Profile.Services.ProfileService;
import com.unipapers.backend.Utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/get")
    private ResponseEntity<?> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        var profileResponse = profileService.getProfile(userDetails.publicId());
        return ResponseEntity.ok(profileResponse);
    }

}
