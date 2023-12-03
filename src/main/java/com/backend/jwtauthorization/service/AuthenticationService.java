package com.backend.jwtauthorization.service;

import com.backend.jwtauthorization.customs.CustomUserDetails;
import com.backend.jwtauthorization.dto.AuthenticationRequestDTO;
import com.backend.jwtauthorization.dto.AuthenticationResponseDTO;
import com.backend.jwtauthorization.dto.UserDTO;
import com.backend.jwtauthorization.entity.Token;
import com.backend.jwtauthorization.entity.User;
import com.backend.jwtauthorization.enums.TokenType;
import com.backend.jwtauthorization.jwt.JwtService;
import com.backend.jwtauthorization.mapper.UserMapper;
import com.backend.jwtauthorization.repository.TokenRepository;
import com.backend.jwtauthorization.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final Logger logger = LogManager.getLogger(AuthenticationService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private  AuthenticationManager authenticationManager;

    public AuthenticationResponseDTO register(UserDTO userDTO) throws Exception {

        logger.info("Registering User");
        try {
            User user = UserMapper.mapDtoToEntity(userDTO);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            CustomUserDetails customUserDetails = new CustomUserDetails(savedUser);
            var jwtToken = jwtService.generateToken(customUserDetails);
            var refreshToken = jwtService.generateRefreshToken(customUserDetails);
            saveUserToken(savedUser, jwtToken);

            logger.info("User Registered Successfully");
            return AuthenticationResponseDTO.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) throws Exception {

        logger.info("Authenticating User Request");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            User user = userRepository.findByEmail(request.getEmail());
            if (user==null) throw new Exception("User Not Found.");
            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            var jwtToken = jwtService.generateToken(customUserDetails);
            var refreshToken = jwtService.generateRefreshToken(customUserDetails);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            logger.info("User Authenticated Successfully");
            return AuthenticationResponseDTO.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();

        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public AuthenticationResponseDTO refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("Refreshing Token");
        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String refreshToken;
            final String userEmail;
            if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
                logger.error("InValid Token.");
                throw new Exception("InValid Token.");
            }
            refreshToken = authHeader.substring(7);
            userEmail = jwtService.extractUsername(refreshToken);
            if (userEmail != null) {
                User user = this.userRepository.findByEmail(userEmail);
                if (user==null) throw new Exception("User Not Found.");
                CustomUserDetails customUserDetails = new CustomUserDetails(user);
                if (jwtService.isTokenValid(refreshToken, customUserDetails)) {
                    var accessToken = jwtService.generateToken(customUserDetails);
                    revokeAllUserTokens(user);
                    saveUserToken(user, accessToken);

                    logger.info("Token Refreshed Successfully");
                    return AuthenticationResponseDTO.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                }
                throw new Exception("Token Not Valid.");
            }else {
                throw new Exception("UserEmail Not Found");
            }
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private void saveUserToken(User user, String jwtToken) throws Exception {
        logger.info("Saving User Token");
        try {
            var token = Token.builder()
                    .user(user)
                    .token(jwtToken)
                    .tokenType(TokenType.BEARER)
                    .expired(false)
                    .revoked(false)
                    .build();
            tokenRepository.save(token);
            logger.info("Token Saved to Respective User.");
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private void revokeAllUserTokens(User user) throws Exception {

        logger.info("Revoking All User's Token");
        try {
            var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
            if (validUserTokens.isEmpty())
                return;
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
            logger.info("Revoke Successful");
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
