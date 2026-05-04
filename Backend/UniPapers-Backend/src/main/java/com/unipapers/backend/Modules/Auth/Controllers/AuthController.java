package com.unipapers.backend.Modules.Auth.Controllers;

import com.unipapers.backend.Modules.Auth.Dtos.LoginRequestDto;
import com.unipapers.backend.Modules.Auth.Dtos.LoginResponseDto;
import com.unipapers.backend.Modules.Auth.Dtos.SignupRequestDto;
import com.unipapers.backend.Modules.Auth.Dtos.VerifyEmailRequestDto;
import com.unipapers.backend.Modules.Auth.Dtos.SendCodeRequestDto;
import com.unipapers.backend.Modules.Auth.Dtos.SendCodeResponseDto;
import com.unipapers.backend.Modules.Auth.Dtos.UpdatePasswordRequestDto;
import com.unipapers.backend.Modules.Auth.Services.AuthService;
import com.unipapers.backend.Modules.Auth.Services.SessionService;
import com.unipapers.backend.Utils.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SessionService sessionService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto signupRequestDto){
        return ResponseEntity.ok().body(Map.of("message", authService.signup(signupRequestDto)));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequestDto verifyEmailRequestDto){
        return ResponseEntity.ok().body(
                Map.of("message", authService.verifyEmail(
                        verifyEmailRequestDto.getEmail(),
                        verifyEmailRequestDto.getVerificationCode()
                ))
        );
    }

    @PostMapping("/resend-verification-code")
    public ResponseEntity<?> resendVerificationCode(@RequestParam("email") String email){
        return ResponseEntity.ok().body(Map.of("message", authService.resendVerificationCode(email)));
    }

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@Valid @RequestBody SendCodeRequestDto sendCodeRequestDto){
        SendCodeResponseDto response = authService.sendVerificationCodeByIdentifier(sendCodeRequestDto.getIdentifier());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequestDto){
        return ResponseEntity.ok().body(authService.login(loginRequestDto));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "access token expired"));
        }
        return ResponseEntity.ok(Map.of("message", "access token still valid"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestParam("refreshToken") String refreshToken) {
        LoginResponseDto response = sessionService.refreshSession(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("refreshToken") String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) {
        return ResponseEntity.ok().body(Map.of("message", authService.updatePassword(updatePasswordRequestDto)));
    }
}
