package com.backend.jwtauthorization.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    private String publicKey;
    private String privateKey;

    {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            publicKey = Base64.encodeBase64String(kp.getPublic().getEncoded());
            privateKey = Base64.encodeBase64String(kp.getPrivate().getEncoded());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public PublicKey generateJwtKeyDecryption(String token, String jwtPublicKey) throws Exception {
        String kid = getHeaderClaims(token, "kid");
        if(kid!=null) return PublicKeys.getPublicKey(kid);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.decodeBase64(jwtPublicKey);
        X509EncodedKeySpec x509EncodedKeySpec=new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    public PrivateKey generateJwtKeyEncryption(String jwtPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.decodeBase64(jwtPrivateKey);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec=new PKCS8EncodedKeySpec(keyBytes);
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
    }

    public String extractUsername(String token) throws Exception {

        logger.info("Extracting Username Claim.");
        try {
            return extractAllClaims(token).get("email", String.class);
        }catch (Exception e){
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    public String extractSender(String jwt) throws Exception {

        logger.info("Extracting Sender Claim.");
        try {
            return extractAllClaims(jwt).get("iss", String.class);
        }catch (Exception e){
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception {
        
        try {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }catch (Exception e) {
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    private Claims extractAllClaims(String token) throws Exception {

        logger.info("Extracting All Claims From Given Token.");
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(generateJwtKeyDecryption(token, publicKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    public String generateToken(UserDetails userDetails) throws Exception {

        logger.info("Generating Token for Given UserDetails.");
        try {
            return generateToken(new HashMap<>(), userDetails);
        }catch (Exception e){
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) throws Exception {
        logger.info("Generating Token for Given UserDetails and ExtraClaims.");
        try {
            return buildToken(extraClaims, userDetails, jwtExpiration);
        }catch (Exception e) {
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) throws Exception {
        logger.info("Generating Refresh Token for Given UserDetails.");
        try {
            return buildToken(new HashMap<>(), userDetails, refreshExpiration);
        }catch (Exception e) {
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) throws Exception {

        logger.info("Building Token for Given userDetails, ExtraClaims and Expiration.");
        try {
            extraClaims.put("email", userDetails.getUsername());
            extraClaims.put("iss", "Local");
            return Jwts
                    .builder()
                    .setClaims(extraClaims)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(generateJwtKeyEncryption(privateKey), SignatureAlgorithm.RS256)
                    .compact();
        }catch (Exception e) {
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) throws Exception {

        logger.info("Checking if Token is Valid for Given UserDetails.");
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        }catch (Exception e) {
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    private boolean isTokenExpired(String token) throws Exception {

        logger.info("Checking If Token Expired.");
        try {
            return extractExpiration(token).before(new Date());
        }catch (Exception e) {
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    private Date extractExpiration(String token) throws Exception {

        logger.info("Extracting Expiration Claim.");
        try {
            return extractClaim(token, Claims::getExpiration);
        }catch (Exception e) {
            throw new Exception(this.getClass() +": "+ e.getMessage());
        }
    }

    public String getHeaderClaims(String token, String claim) {

        String[] components = token.split("\\.");
        JSONObject header = new JSONObject(new String(java.util.Base64.getDecoder().decode(components[0])));
        if(header.has(claim)) return String.valueOf(header.get(claim));
        return null;
    }

}
