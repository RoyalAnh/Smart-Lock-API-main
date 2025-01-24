package com.labmanager.project.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

import org.apache.commons.codec.binary.Base32;

public class AESUtil {

    private static final String AES = "AES/ECB/PKCS5Padding";
    private static final int KEY_LENGTH = 16;
    private static final ThreadLocal<Cipher> ENCRYPT_CIPHER = ThreadLocal.withInitial(() -> {
        try {
            return Cipher.getInstance(AES);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize encryption cipher", e);
        }
    });
    private static final ThreadLocal<Cipher> DECRYPT_CIPHER = ThreadLocal.withInitial(() -> {
        try {
            return Cipher.getInstance(AES);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize decryption cipher", e);
        }
    });

    private static SecretKey getSecretKey(String secretKey) {
        secretKey = ensureKeyLength(secretKey);
        return new SecretKeySpec(secretKey.getBytes(), "AES");
    }

    private static String ensureKeyLength(String key) {
        if (key.length() < KEY_LENGTH) {
            char[] padding = new char[KEY_LENGTH - key.length()];
            Arrays.fill(padding, '0');
            return key + new String(padding);
        } else if (key.length() > KEY_LENGTH) {
            return key.substring(0, KEY_LENGTH);
        }
        return key;
    }

    public static String encodeBase32(byte[] data) {
        Base32 base32 = new Base32();
        String encoded = base32.encodeToString(data).replace("=", "");
        if (encoded.length() > 16) {
            return encoded.substring(0, 16);
        } else if (encoded.length() < 16) {
            return String.format("%-16s", encoded).replace(' ', '0');
        }
        return encoded;
    }

    private static byte[] decodeBase32(String data) {
        Base32 base32 = new Base32();
        return base32.decode(data);
    }

    public static String encrypt(String data, String secretKey) throws Exception {
        Cipher cipher = ENCRYPT_CIPHER.get();
        SecretKey key = getSecretKey(secretKey);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, String secretKey) throws Exception {
        Cipher cipher = DECRYPT_CIPHER.get();
        SecretKey key = getSecretKey(secretKey);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}