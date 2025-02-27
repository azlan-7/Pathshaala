package com.example.loginpage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionHelper {
    private static final String MASTER_KEY = "MySuperSecureMasterKey2025"; // Store securely

    // Generate AES key (256-bit)
    public static byte[] generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }

    // Encrypt data using AES key
    public static String encryptData(String plainText, byte[] aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");

        // Generate a new IV
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // Decrypt data using AES key
    public static String decryptData(String cipherText, byte[] aesKey) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(cipherText);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");

        // Extract IV from the data
        byte[] iv = new byte[16];
        System.arraycopy(decoded, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted data
        byte[] encryptedData = new byte[decoded.length - iv.length];
        System.arraycopy(decoded, iv.length, encryptedData, 0, encryptedData.length);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decrypted = cipher.doFinal(encryptedData);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // Encrypt AES Key using Master Key
    public static String encryptAESKey(byte[] aesKey) throws Exception {
        byte[] masterKeyHash = hashMasterKey(MASTER_KEY);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(masterKeyHash, "AES");

        // Generate IV
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedKey = cipher.doFinal(aesKey);

        // Combine IV and encrypted key
        byte[] combined = new byte[iv.length + encryptedKey.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedKey, 0, combined, iv.length, encryptedKey.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    // Decrypt AES Key using Master Key
    public static byte[] decryptAESKey(String encryptedKey) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedKey);
        byte[] masterKeyHash = hashMasterKey(MASTER_KEY);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(masterKeyHash, "AES");

        // Extract IV
        byte[] iv = new byte[16];
        System.arraycopy(decoded, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Extract encrypted key
        byte[] encryptedData = new byte[decoded.length - iv.length];
        System.arraycopy(decoded, iv.length, encryptedData, 0, encryptedData.length);

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return cipher.doFinal(encryptedData);
    }

    // Hash Master Key using SHA-256 (for AES-256 key encryption)
    private static byte[] hashMasterKey(String masterKey) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(masterKey.getBytes(StandardCharsets.UTF_8));
    }

    // Testing the functions
    public static void main(String[] args) {
        try {
            String originalText = "Hello, Secure World!";
            byte[] aesKey = generateAESKey();

            // Encrypt & Decrypt
            String encryptedData = encryptData(originalText, aesKey);
            String decryptedData = decryptData(encryptedData, aesKey);

            System.out.println("Original: " + originalText);
            System.out.println("Encrypted: " + encryptedData);
            System.out.println("Decrypted: " + decryptedData);

            // Encrypt & Decrypt AES Key
            String encryptedAESKey = encryptAESKey(aesKey);
            byte[] decryptedAESKey = decryptAESKey(encryptedAESKey);

            System.out.println("AES Key Encrypted: " + encryptedAESKey);
            System.out.println("AES Key Decrypted (Matches Original): " + java.util.Arrays.equals(aesKey, decryptedAESKey));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}