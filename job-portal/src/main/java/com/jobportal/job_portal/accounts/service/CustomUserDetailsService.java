package com.jobportal.job_portal.accounts.service;

import com.jobportal.job_portal.accounts.entity.CustomUser;
import com.jobportal.job_portal.accounts.repository.CustomUserRepository;
import com.jobportal.job_portal.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomUserRepository userRepository;

    public CustomUserDetailsService(CustomUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Utilisateur non trouvé : " + username, HttpStatus.NOT_FOUND));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (Boolean.TRUE.equals(user.getIsRecruiter())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_RECRUITER"));
        }
        if (Boolean.TRUE.equals(user.getIsAdmin())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}