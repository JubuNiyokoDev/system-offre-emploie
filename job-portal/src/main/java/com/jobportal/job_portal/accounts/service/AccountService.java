package com.jobportal.job_portal.accounts.service;

import com.jobportal.job_portal.accounts.dto.RegisterDTO;
import com.jobportal.job_portal.accounts.dto.UpdateProfileDTO;
import com.jobportal.job_portal.accounts.dto.UserDTO;
import com.jobportal.job_portal.accounts.entity.CustomUser;
import com.jobportal.job_portal.accounts.repository.CustomUserRepository;
import com.jobportal.job_portal.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final CustomUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(CustomUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO registerUser(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getPasswordConfirm())) {
            throw new ApiException("Les mots de passe ne correspondent pas", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new ApiException("Ce nom d'utilisateur est déjà pris", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new ApiException("Cet email est déjà utilisé", HttpStatus.BAD_REQUEST);
        }

        CustomUser user = CustomUser.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .isRecruiter(registerDTO.getIsRecruiter())
                .isAdmin(false)
                .isSuperuser(false)
                .isBanned(false)
                .build();

        user = userRepository.save(user);
        return toUserDTO(user);
    }

    public UserDTO updateProfile(String username, UpdateProfileDTO updateProfileDTO) {
        CustomUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Utilisateur non trouvé : " + username, HttpStatus.NOT_FOUND));

        if (updateProfileDTO.getUsername() != null && !updateProfileDTO.getUsername().equals(username)) {
            if (userRepository.existsByUsername(updateProfileDTO.getUsername())) {
                throw new ApiException("Ce nom d'utilisateur est déjà pris", HttpStatus.BAD_REQUEST);
            }
            user.setUsername(updateProfileDTO.getUsername());
        }
        if (updateProfileDTO.getEmail() != null && !updateProfileDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateProfileDTO.getEmail())) {
                throw new ApiException("Cet email est déjà utilisé", HttpStatus.BAD_REQUEST);
            }
            user.setEmail(updateProfileDTO.getEmail());
        }
        if (updateProfileDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(updateProfileDTO.getPhoneNumber());
        }

        user = userRepository.save(user);
        return toUserDTO(user);
    }

    public UserDTO toggleBan(String username) {
        CustomUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Utilisateur non trouvé : " + username, HttpStatus.NOT_FOUND));
        user.setIsBanned(!user.getIsBanned());
        user = userRepository.save(user);
        return toUserDTO(user);
    }

    public UserDTO getUser(String username) {
        CustomUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Utilisateur non trouvé : " + username, HttpStatus.NOT_FOUND));
        return toUserDTO(user);
    }

    private UserDTO toUserDTO(CustomUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setIsRecruiter(user.getIsRecruiter());
        userDTO.setIsBanned(user.getIsBanned());
        return userDTO;
    }
}