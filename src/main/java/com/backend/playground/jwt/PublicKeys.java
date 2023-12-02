package com.backend.playground.jwt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

@Service
public class PublicKeys {

    private static final Logger logger = LogManager.getLogger(PublicKeys.class);
    private static String MODULUS;
    private static String EXPONENT;

    private static RestTemplate restTemplate = new RestTemplate();

    private static void getParams(String kid) {
        try {
            Keys list = restTemplate.getForObject("https://www.googleapis.com/oauth2/v3/certs", Keys.class);
            for(Key k : list.getKeys()){
                if(k.getKid().equals(kid)) {
                    MODULUS = k.getN();
                    EXPONENT = k.getE();
                    break;
                }
            }
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public static byte[] base64UrlDecodeToBytes(String input)
    {
        Base64 decoder = new Base64(-1, null, true);
        byte[] decodedBytes = decoder.decode(input);
        return decodedBytes;
    }
    public static PublicKey getPublicKey(String kid) {
        getParams(kid);
        byte[] nb = base64UrlDecodeToBytes(MODULUS);
        byte[] eb = base64UrlDecodeToBytes(EXPONENT);
        BigInteger n = new BigInteger(1, nb);
        BigInteger e = new BigInteger(1, eb);
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
        try
        {
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(rsaPublicKeySpec);
            return publicKey;
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Cant create public key", ex);
        }
    }
}
