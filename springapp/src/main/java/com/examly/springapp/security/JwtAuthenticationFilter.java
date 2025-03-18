package com.examly.springapp.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final String[] PERMITTED_PATHS = {
        "/api/users/register",
        "/api/users/login",
        "/api/users/logout",
        "/swagger-ui",
        "/v3/api-docs"
    };

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        logger.debug("Processing request for URI: " + requestURI);

        boolean isPermitted = false;
        for (String path : PERMITTED_PATHS) {
            if (requestURI.startsWith(path)) {
                isPermitted = true;
                logger.debug("Skipping JWT validation for permitted path: " + requestURI);
                break;
            }
        }

        if (!isPermitted) {
            String header = request.getHeader("Authorization");
            logger.debug("Authorization header: " + header);

            if (header != null && header.startsWith("Bearer ")) {
                try {
                    String token = header.substring(7);
                    logger.debug("Extracted JWT token: " + token);
                    String username = jwtUtil.getUsernameFromToken(token);
                    logger.debug("Extracted username from token: " + username);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        logger.debug("Loaded user details: " + userDetails.getUsername());

                        if (jwtUtil.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.debug("Set authentication for user: " + username);
                        } else {
                            logger.warn("Invalid or blacklisted JWT token for URI: " + requestURI);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            return;
                        }
                    }
                } catch (Exception e) {
                    logger.error("JWT validation failed: ", e);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            } else {
                logger.warn("No valid JWT token found for URI: " + requestURI);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}