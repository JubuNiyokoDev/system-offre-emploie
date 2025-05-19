package com.jobportal.job_portal.security;

import com.jobportal.job_portal.common.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
                logger.debug("JWT extrait : {}", jwt);
                username = jwtUtil.extractUsername(jwt);
                logger.debug("Nom d'utilisateur extrait : {}", username);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, username)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Utilisateur authentifié : {}", username);
                } else {
                    logger.warn("Validation du token échouée pour l'utilisateur : {}", username);
                }
            }
        } catch (ApiException e) {
            logger.error("Erreur de validation du token : {}", e.getMessage());
            response.setStatus(e.getStatus().value());
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                    "{\"status\":\"error\",\"message\":\"%s\",\"data\":null}", e.getMessage()));
            return;
        } catch (Exception e) {
            logger.error("Erreur inattendue dans le filtre JWT : {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}