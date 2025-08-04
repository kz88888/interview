package com.qsystem.clientlib;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 密码加密类
 */
public class PwdEncryption {
    public static String key = "A7zb534OPq59gU7q";
    
    private static Cipher getCipher(String secretKey) throws Exception {
        byte[] keyBytes = new byte[16];
        byte[] secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(secretKeyBytes, 0, keyBytes, 0, Math.min(keyBytes.length, secretKeyBytes.length));
        
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(keyBytes);
        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        
        return cipher;
    }
    
    private static byte[] encrypt(byte[] plainBytes, Cipher cipher) throws Exception {
        return cipher.doFinal(plainBytes);
    }
    
    public static String Encrypt(String plainText) {
        try {
            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(encrypt(plainBytes, getCipher(key)));
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }
    
    public static String Decrypt(String encryptedText) {
        try {
            byte[] keyBytes = new byte[16];
            byte[] secretKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            System.arraycopy(secretKeyBytes, 0, keyBytes, 0, Math.min(keyBytes.length, secretKeyBytes.length));
            
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(keyBytes);
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }
} 