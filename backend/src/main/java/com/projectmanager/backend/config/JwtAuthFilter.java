package com.projectmanager.backend.config;

import com.projectmanager.backend.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        extractToken(request).ifPresent(this::authenticateFromToken);
        chain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!isBearerToken(header)) {
            return Optional.empty();
        }
        return Optional.of(header.substring(7));
    }

    private boolean isBearerToken(String header) {
        return header != null && header.startsWith("Bearer ");
    }

    private void authenticateFromToken(String token) {
        try {
            Long userId = jwtService.extractUserId(token);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    String.valueOf(userId), null, List.of()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException ignored) {
        }
    }
}
