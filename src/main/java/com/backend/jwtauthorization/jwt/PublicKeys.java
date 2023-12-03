package com.backend.jwtauthorization.jwt;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

public class PublicKeys {
    private static String MODULUS;
    private static String EXPONENT;
    private final static RestTemplate restTemplate = new RestTemplate();

    private static void getParams(String kid) throws Exception {
        try {
            Keys list = restTemplate.getForObject("https://www.googleapis.com/oauth2/v3/certs", Keys.class);
            assert list != null;
            for(Key k : list.getKeys()){
                if(k.getKid().equals(kid)) {
                    MODULUS = k.getN();
                    EXPONENT = k.getE();
                    break;
                }
            }
        }catch (Exception e) {
            throw new Exception(PublicKeys.class+": "+e.getMessage());
        }
    }

    private static byte[] base64UrlDecodeToBytes(String input)
    {
        Base64 decoder = new Base64(-1, null, true);
        return decoder.decode(input);
    }
    public static PublicKey getPublicKey(String kid) throws Exception {
        getParams(kid);
        byte[] nb = base64UrlDecodeToBytes(MODULUS);
        byte[] eb = base64UrlDecodeToBytes(EXPONENT);
        BigInteger n = new BigInteger(1, nb);
        BigInteger e = new BigInteger(1, eb);
        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(n, e);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(rsaPublicKeySpec);
        } catch (Exception exception) {
            throw new Exception(PublicKeys.class+": "+exception.getMessage());
        }
    }
}
