package com.backend.playground.jwt;

import com.backend.playground.customs.CustomUserDetails;
import com.backend.playground.customs.CustomUserDetailsService;
import com.backend.playground.entity.Token;
import com.backend.playground.enums.TokenType;
import com.backend.playground.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final Logger logger = LogManager.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println(request.getServletPath());
        if (request.getServletPath().contains("/swagger-ui") || request.getServletPath().contains("/api/v1/auth") || request.getServletPath().contains("/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");
//        System.out.println(authHeader);
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            logger.error("Please Provide Valid Token.");
            return;
        }
        final String jwt;
        final String userEmail;
        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            logger.error(e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println(userEmail);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            CustomUserDetails customUserDetails = this.customUserDetailsService.loadUserByUsername(userEmail);

            try {
                if (jwtService.isTokenValid(jwt, customUserDetails)) {

                    final String sender;
                    try {
                        sender = jwtService.extractSender(jwt);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        filterChain.doFilter(request, response);
                        return;
                    }

                    if(!sender.equals("Local")) {
                        Token new_token = new Token(null, jwt, TokenType.BEARER, false, false, customUserDetails.getUser());
                        tokenRepository.save(new_token);
                    }

                    var isTokenValid = tokenRepository.findByToken(jwt)
                            .map(t -> !t.isExpired() && !t.isRevoked())
                            .orElse(false);

                    if(!isTokenValid) {
                        logger.error("Token is not valid");
                        filterChain.doFilter(request, response);
                        return;
                    }

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            customUserDetails,
                            null,
                            customUserDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                filterChain.doFilter(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }


}
