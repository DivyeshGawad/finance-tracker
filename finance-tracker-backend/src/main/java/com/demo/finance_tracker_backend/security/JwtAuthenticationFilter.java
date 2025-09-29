package com.demo.finance_tracker_backend.security;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.demo.finance_tracker_backend.dto.ApiResponse;
import com.demo.finance_tracker_backend.entity.UserEntity;
import com.demo.finance_tracker_backend.exception.UnauthorizedException;
import com.demo.finance_tracker_backend.repository.UserRepository;
import com.demo.finance_tracker_backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // ✅ Missing or invalid header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            handleJwtError(response, "JWT token is missing, please login first");
            return;
        }
            String token = authHeader.substring(7);

            try {
                // Extract username (will throw ExpiredJwtException if expired)
                String username = jwtUtil.extractUsername(token);
                
                // Fetch user from DB
                UserEntity user  = userRepository.findByUsername(username)
                		.orElseThrow(() -> new UnauthorizedException("User not found"));

                // Validate TokenVerison
                String tokenVersionFromToken = jwtUtil.extractTokenVersion(token);
                if(!tokenVersionFromToken.equals(String.valueOf(user.getTokenVersion()))) {
                	throw new UnauthorizedException("JWT Token is invalid or outdated");
                }
                
                // Extract roles
                Set<SimpleGrantedAuthority> authorities = jwtUtil.extractRoles(token).stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toSet());

                // ✅ Use CustomUserDetails as principal
                CustomUserDetails principal = new CustomUserDetails(
                        user.getUserId(),
                        user.getUsername(),
                        null, // password not needed here
                        authorities
                );
                // Set authentication
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                handleJwtError(response, "JWT token has expired, please login again");
                return;
            } catch (UnauthorizedException ex) {
                handleJwtError(response, ex.getMessage());
                return;
            } catch (Exception ex) {
                handleJwtError(response, "Invalid JWT token");
                return;
            }
        

        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // Public endpoints that don't need JWT
        return path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/register")
                || path.startsWith("/api/auth/verify")
                || path.startsWith("/api/auth/forgot-username")
                || path.startsWith("/api/auth/forgot-password")
                || path.startsWith("/api/auth/reset-password");
    }

    
    private void handleJwtError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                objectMapper.writeValueAsString(ApiResponse.error(message))
        );
    }
}
