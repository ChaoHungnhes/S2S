package com.example.S2S.common.Util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncrDecrUtils {


        private static final String SECRET_KEY = "mustbe16byteskey"; // 16 bytes key
        private static final String ALGORITHM = "AES";

        /**
         * Encrypts a plain text using AES encryption.
         *
         * @param plainText The text to be encrypted.
         * @return The encrypted text in Base64 format.
         */
        public static String encrypt(String plainText) {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } catch (Exception e) {
                throw new RuntimeException("Error while encrypting: " + e.getMessage());
            }
        }

        /**
         * Decrypts an encrypted text using AES decryption.
         *
         * @param encryptedText The encrypted text in Base64 format.
         * @return The original plain text.
         */
        public static String decrypt(String encryptedText) {
            try {
                SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
                byte[] decryptedBytes = cipher.doFinal(decodedBytes);
                return new String(decryptedBytes);
            } catch (Exception e) {
                throw new RuntimeException("Error while decrypting: " + e.getMessage());
            }
        }
    }

