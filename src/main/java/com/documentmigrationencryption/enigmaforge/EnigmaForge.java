package com.documentmigrationencryption.enigmaforge;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EnigmaForge {
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~!@#$%^&*()_+-=[]{}|;':\",./<>?";
    private static final String AES_ALGORITHM = "AES";
    private static final String BLOWFISH_ALGORITHM = "Blowfish";
    private static final String TRIPLE_DES_ALGORITHM = "DESede";
    private static final String AES_MODE = "AES/CBC/PKCS5Padding";
    private static final String BLOWFISH_MODE = "Blowfish/CBC/PKCS5Padding";
    private static final String TRIPLE_DES_MODE = "DESede/CBC/PKCS5Padding";
    private static final String AES_IV_PARAMETER = "0123456789ABCDEF";
    private static final String BLOWFISH_IV_PARAMETER = "01234567";
    private static final String TRIPLE_DES_IV_PARAMETER = "01234567";

    private static String AES_KEY;
    private static String BLOWFISH_KEY;
    private static String TRIPLE_DES_KEY;

    public EnigmaForge() {
    }

    public static void main(String[] args) {
        System.out.println("EnigmaForge Successful");
    }

    public static byte[] generateAesKey() {
        return generateKey(32);
    }

    public static byte[] generateBfKey() {
        return generateKey(32);
    }

    public static byte[] generateDesKey() {
        return generateKey(24);
    }

    private static byte[] generateKey(int keySize) {
        byte[] randomBytes = new byte[keySize];
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < keySize; ++i) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            randomBytes[i] = (byte) ALLOWED_CHARACTERS.charAt(randomIndex);
        }

        return randomBytes;
    }

    public static byte[] encrypt(byte[] plaintext, byte[] aesKey, byte[] bfKey, byte[] desKey) throws Exception {
        AES_KEY = new String(aesKey, StandardCharsets.UTF_8);
        BLOWFISH_KEY = new String(bfKey, StandardCharsets.UTF_8);
        TRIPLE_DES_KEY = new String(desKey, StandardCharsets.UTF_8);

        byte[] ciphertext1 = encryptAlgorithm(plaintext, AES_ALGORITHM, AES_KEY, AES_IV_PARAMETER, AES_MODE);
        byte[] ciphertext2 = encryptAlgorithm(ciphertext1, BLOWFISH_ALGORITHM, BLOWFISH_KEY, BLOWFISH_IV_PARAMETER, BLOWFISH_MODE);
        return encryptAlgorithm(ciphertext2, TRIPLE_DES_ALGORITHM, TRIPLE_DES_KEY, TRIPLE_DES_IV_PARAMETER, TRIPLE_DES_MODE);
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] aesKey, byte[] bfKey, byte[] desKey) throws Exception {
        AES_KEY = new String(aesKey, StandardCharsets.UTF_8);
        BLOWFISH_KEY = new String(bfKey, StandardCharsets.UTF_8);
        TRIPLE_DES_KEY = new String(desKey, StandardCharsets.UTF_8);

        byte[] plaintext1 = decryptAlgorithm(ciphertext, TRIPLE_DES_ALGORITHM, TRIPLE_DES_KEY, TRIPLE_DES_IV_PARAMETER, TRIPLE_DES_MODE);
        byte[] plaintext2 = decryptAlgorithm(plaintext1, BLOWFISH_ALGORITHM, BLOWFISH_KEY, BLOWFISH_IV_PARAMETER, BLOWFISH_MODE);
        return decryptAlgorithm(plaintext2, AES_ALGORITHM, AES_KEY, AES_IV_PARAMETER, AES_MODE);
    }

    private static byte[] encryptAlgorithm(byte[] input, String algorithm, String keyString, String ivParameter, String mode) throws Exception {
        Cipher cipher = Cipher.getInstance(mode);
        SecretKeySpec keySpec = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8), algorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(ivParameter.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(input);
    }

    private static byte[] decryptAlgorithm(byte[] input, String algorithm, String keyString, String ivParameter, String mode) throws Exception {
        Cipher cipher = Cipher.getInstance(mode);
        SecretKeySpec keySpec = new SecretKeySpec(keyString.getBytes(StandardCharsets.UTF_8), algorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(ivParameter.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(input);
    }

    public static String encryptText(String message, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : message.toCharArray()) {
            result.append((char) (c + shift));
        }
        return result.toString();
    }

    public static String decryptText(String message, int shift) {
        StringBuilder result = new StringBuilder();
        for (char c : message.toCharArray()) {
            result.append((char) (c - shift));
        }
        return result.toString();
    }
}
