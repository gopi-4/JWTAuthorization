package com.backend.playground.service;

import com.backend.playground.dto.AuthenticationRequestDTO;
import com.backend.playground.dto.AuthenticationResponseDTO;
import com.backend.playground.dto.UserDTO;
import com.backend.playground.entity.Token;
import com.backend.playground.entity.User;
import com.backend.playground.enums.TokenType;
import com.backend.playground.jwt.JwtService;
import com.backend.playground.mapper.UserMapper;
import com.backend.playground.repository.TokenRepository;
import com.backend.playground.repository.UserRepository;
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

    public AuthenticationResponseDTO register(UserDTO userDTO) {

        logger.info("Registering User");
        try {
            User user = UserMapper.mapDtoToEntity(userDTO);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            var savedUser = userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(savedUser, jwtToken);

            logger.info("User Registered Successfully");
            return AuthenticationResponseDTO.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }catch (Exception e) {
            logger.error(e.getMessage());
            return AuthenticationResponseDTO.builder().build();
        }
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {

        logger.info("Authenticating User Request");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            logger.info("User Authenticated Successfully");
            return AuthenticationResponseDTO.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();

        }catch (Exception e) {
            logger.error(e.getMessage());
            return AuthenticationResponseDTO.builder().build();
        }
    }

    private void saveUserToken(User user, String jwtToken) {
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
            logger.info("Token Saved to Respective User");
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void revokeAllUserTokens(User user) {

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
        }
    }

    public AuthenticationResponseDTO refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("Refreshing Token");
        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String refreshToken;
            final String userEmail;
            if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
                logger.info("Please Provide Correct Bearer Token");
                throw new Exception("Bad Request");
            }
            refreshToken = authHeader.substring(7);
            userEmail = jwtService.extractUsername(refreshToken);
            if (userEmail != null) {
                var user = this.userRepository.findByEmail(userEmail).orElseThrow();
                if (jwtService.isTokenValid(refreshToken, user)) {
                    var accessToken = jwtService.generateToken(user);
                    revokeAllUserTokens(user);
                    saveUserToken(user, accessToken);
                    logger.info("Token Refreshed Successfully");
//                    var authResponse = AuthenticationResponseDTO.builder()
//                            .accessToken(accessToken)
//                            .refreshToken(refreshToken)
//                            .build();
//                    new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
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
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
