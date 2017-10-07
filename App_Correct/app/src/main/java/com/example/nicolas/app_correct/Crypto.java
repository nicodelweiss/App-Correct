package com.example.nicolas.app_correct;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    /**

     * @param args

     */

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private static Formatter formatter;

    private static String toHexString(byte[] bytes) {

        formatter = new Formatter();


        for (byte b : bytes) {

            formatter.format("%02x", b);

        }

        return formatter.toString();

    }

    public static String calculateRFC2104HMAC(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {

        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);

        mac.init(signingKey);

        return toHexString(mac.doFinal(data.getBytes()));

    }
}
