package com.backend.playground.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
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

    private final Logger logger = LogManager.getLogger(JwtService.class);
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private final String publicKey;
    private final String privateKey;

    {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            publicKey = Base64.encodeBase64String(kp.getPublic().getEncoded());
            privateKey = Base64.encodeBase64String(kp.getPrivate().getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey generateJwtKeyDecryption(String jwtPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
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

    private Claims extractAllClaims(String token) throws Exception {

        logger.info("Extracting All Claims From Given Token.");
        try {
//            logger.info("All Claims Extracted Successfully for Given Token.");
            return Jwts
                    .parserBuilder()
                    .setSigningKey(generateJwtKeyDecryption(publicKey))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

//    public boolean validateJwtToken(String authToken,String jwtPublicKey) {
//        try {
//            Jwts.parser().setSigningKey(generateJwtKeyDecryption(jwtPublicKey)).parseClaimsJws(authToken);
//            return true;
//        } catch (SignatureException e) {
//            System.out.println("Invalid JWT signature: {}"+ e.getMessage());
//        } catch (MalformedJwtException e) {
//            System.out.println("Invalid JWT token: {}"+ e.getMessage());
//        } catch (ExpiredJwtException e) {
//            System.out.println("JWT token is expired: {}"+ e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            System.out.println("JWT token is unsupported: {}"+ e.getMessage());
//        } catch (IllegalArgumentException e) {
//            System.out.println("JWT claims string is empty: {}"+ e.getMessage());
//        } catch (NoSuchAlgorithmException e) {
//            System.out.println("no such algorithm exception");
//        } catch (InvalidKeySpecException e) {
//            System.out.println("invalid key exception");
//        }
//
//        return false;
//    }

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

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) throws Exception {
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

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) throws Exception {

        logger.info("Building Token for Given userDetails, ExtraClaims and Expiration.");
        try {
//            logger.info("Token Build for Given userDetails, ExtraClaims and Expiration.");
            return Jwts
                    .builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(SignatureAlgorithm.RS256, generateJwtKeyEncryption(privateKey))
                    .compact();
        }catch (Exception e) {
//            logger.error(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

//    public String generateAccessToken(String userName, List<String> roleArray, String jwtPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        return Jwts.builder()
//                .setSubject(userName)
//                .claim("roles", roleArray)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date()).getTime() + accessExpirationMs))
//                .signWith(SignatureAlgorithm.RS256, generateJwtKeyEncryption(jwtPrivateKey))
//                .compact();
//    }

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

//    private Key getSignInKey() throws Exception {
//
//        logger.info("Getting SignIn key From Secret Key.");
//        try {
////            logger.info("Obtained SignIn Key.");
//            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//            return Keys.secretKeyFor(SignatureAlgorithm.RS256);
//        }catch (Exception e) {
////            logger.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }
}
