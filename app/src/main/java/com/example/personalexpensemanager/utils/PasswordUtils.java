package com.example.personalexpensemanager.utils;

import java.security.MessageDigest;

public class PasswordUtils {

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password;
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null) return false;
        String hashedInput = hashPassword(password);
        return hashedInput.equalsIgnoreCase(storedHash);
    }
}