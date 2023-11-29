package com.backend.playground.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final Logger logger = LogManager.getLogger(JwtService.class);
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) throws Exception {

        logger.info("Extracting Username Claim.");
        try {
//            logger.info("Username Claim Extracted Successfully.");
            return extractClaim(token, Claims::getSubject);
        }catch (Exception e){
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception {

        logger.info("Extracting Claim for Given claimsResolver.");
        try {
//            logger.info("Claim Extracted Successfully for Given claimsResolver.");
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }catch (Exception e) {
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public String generateToken(UserDetails userDetails) throws Exception {

        logger.info("Generating Token for Given UserDetails.");
        try {
//            logger.info("Token Generated Successfully for Given UserDetails.");
            return generateToken(new HashMap<>(), userDetails);
        }catch (Exception e){
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) throws Exception {
        logger.info("Generating Token for Given UserDetails and ExtraClaims.");
        try {
//            logger.info("Token Generated Successfully for Given UserDetails and ExtraClaims.");
            return buildToken(extraClaims, userDetails, jwtExpiration);
        }catch (Exception e) {
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) throws Exception {
        logger.info("Generating Refresh Token for Given UserDetails.");
        try {
//            logger.info("Refresh Token Generated Successfully for Given UserDetails.");
            return buildToken(new HashMap<>(), userDetails, refreshExpiration);
        }catch (Exception e) {
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) throws Exception {

        logger.info("Building Token for Given userDetails, ExtraClaims and Expiration.");
        try {
//            logger.info("Token Build for Given userDetails, ExtraClaims and Expiration.");
            return Jwts
                    .builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        }catch (Exception e) {
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws Exception {

        logger.info("Checking if Token is Valid for Given UserDetails.");
        try {
//            logger.info("Token is valid for Given UserDetails.");
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        }catch (Exception e) {
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private boolean isTokenExpired(String token) throws Exception {

        logger.info("Checking If Token Expired.");
        try {
//            logger.info("Token not Expired.");
            return extractExpiration(token).before(new Date());
        }catch (Exception e) {
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private Date extractExpiration(String token) throws Exception {

        logger.info("Extracting Expiration Claim.");
        try {
//            logger.info("Expiration Claim Extracted Successfully.");
            return extractClaim(token, Claims::getExpiration);
        }catch (Exception e) {
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private Claims extractAllClaims(String token) throws Exception {

        logger.info("Extracting All Claims From Given Token.");
        try {
//            logger.info("All Claims Extracted Successfully for Given Token.");
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    private Key getSignInKey() throws Exception {

        logger.info("Getting SignIn key From Secret Key.");
        try {
//            logger.info("Obtained SignIn Key.");
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        }catch (Exception e) {
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}
