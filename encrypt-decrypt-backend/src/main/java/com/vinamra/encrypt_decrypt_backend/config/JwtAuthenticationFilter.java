package com.vinamra.encrypt_decrypt_backend.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Added import
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vinamra.encrypt_decrypt_backend.service.impl.AdminDetailsService;
import com.vinamra.encrypt_decrypt_backend.util.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Added for logging

@Component
@RequiredArgsConstructor
@Slf4j // Enable logging for this class
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AdminDetailsService adminDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // 1. Check for Authorization header presence and format
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found or token format is invalid for request: {}", request.getRequestURI());
            filterChain.doFilter(request, response); // Continue filter chain, let other filters/security context handle
            return;
        }

        String token = authHeader.substring(7); // Extract token (remove "Bearer ")
        String userName = null;

        try {
            userName = jwtUtils.getUserNameFromToken(token);
        } catch (Exception e) {
            log.warn("Invalid JWT token for request: {}. Error: {}", request.getRequestURI(), e.getMessage());
            // It's generally better to let the filter chain continue and let subsequent
            // security mechanisms (e.g., Spring Security's ExceptionTranslationFilter)
            // handle authentication failures and return appropriate HTTP status codes (401/403).
            // Do NOT throw an exception here unless you have specific error handling in this filter.
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Validate userName and check if already authenticated
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Attempting to authenticate user: {}", userName);
            UserDetails userDetails = adminDetailsService.loadUserByUsername(userName);

            // 3. Validate token against UserDetails (expiration, signature, username match)
            if (jwtUtils.isTokenValid(token, userDetails)) { // Pass userDetails to isTokenValid for robust validation
                log.debug("JWT token is valid for user: {}", userName);

                // 4. Create authentication token and set in SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Set authentication details for better logging and auditing
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("User '{}' authenticated successfully via JWT.", userName);
            } else {
                log.warn("JWT token is invalid or expired for user: {}", userName);
            }
        }

        // 5. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}