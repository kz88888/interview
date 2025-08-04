package com.qsystem.clientlib;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 工具类
 */
public class Utils {
    
    public static String Sha1(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] data = md.digest(str.getBytes("UTF-8"));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : data) {
                sb.append(String.format("%02X", b));
            }
            
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA1计算失败", e);
        }
    }
    
    public static <T> T ReadToObject(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }
} 