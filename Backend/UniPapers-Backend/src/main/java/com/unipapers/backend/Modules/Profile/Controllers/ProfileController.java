package com.unipapers.backend.Modules.Profile.Controllers;

import com.unipapers.backend.Modules.Profile.Services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/get")
    // TODO: Refactor this to get the publicId using the @AuthenticationPrincipal
    private ResponseEntity<?> getProfile(@RequestParam("publicId") String publicId) {
        var profileResponse = profileService.getProfile(publicId);
        return ResponseEntity.ok(profileResponse);
    }

}
