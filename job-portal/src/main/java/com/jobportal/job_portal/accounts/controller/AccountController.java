package com.jobportal.job_portal.accounts.controller;

import com.jobportal.job_portal.accounts.dto.LoginDTO;
import com.jobportal.job_portal.accounts.dto.RefreshTokenDTO;
import com.jobportal.job_portal.accounts.dto.RegisterDTO;
import com.jobportal.job_portal.accounts.dto.UpdateProfileDTO;
import com.jobportal.job_portal.accounts.dto.UserDTO;
import com.jobportal.job_portal.accounts.service.AccountService;
import com.jobportal.job_portal.security.IsAdminOrSelf;
import com.jobportal.job_portal.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AccountController(AccountService accountService, AuthenticationManager authenticationManager,
            JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody RegisterDTO registerDTO) {
        UserDTO userDTO = accountService.registerUser(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("success", "Inscription réussie", userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
        return ResponseEntity.ok(new LoginResponse("success", "Connexion réussie", accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        String accessToken = jwtUtil.refreshAccessToken(refreshTokenDTO.getRefreshToken());
        String newRefreshToken = jwtUtil.rotateRefreshToken(refreshTokenDTO.getRefreshToken());
        return ResponseEntity.ok(new LoginResponse("success", "Token rafraîchi", accessToken, newRefreshToken));
    }

    @GetMapping("/{username}")
    @IsAdminOrSelf
    public ResponseEntity<?> getUser(@PathVariable String username) {
        UserDTO userDTO = accountService.getUser(username);
        return ResponseEntity.ok(new ApiResponse("success", "Utilisateur récupéré", userDTO));
    }

    @PatchMapping("/{username}/update")
    @IsAdminOrSelf
    public ResponseEntity<?> updateProfile(@PathVariable String username,
            @Valid @RequestBody UpdateProfileDTO updateProfileDTO) {
        UserDTO userDTO = accountService.updateProfile(username, updateProfileDTO);
        return ResponseEntity.ok(new ApiResponse("success", "Profil mis à jour", userDTO));
    }

    @PatchMapping("/{username}/toggle-ban")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERUSER')")
    public ResponseEntity<?> toggleBan(@PathVariable String username) {
        UserDTO userDTO = accountService.toggleBan(username);
        return ResponseEntity.ok(new ApiResponse("success", "Statut de bannissement modifié", userDTO));
    }

    // Classes internes pour structurer les réponses
    record ApiResponse(String status, String message, Object data) {
    }

    record LoginResponse(String status, String message, String accessToken, String refreshToken) {
    }
}