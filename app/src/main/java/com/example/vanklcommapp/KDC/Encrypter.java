package com.example.vanklcommapp.KDC;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {

    // Constructor
    public Encrypter() {
        // Default constructor
    }

    // Method to encrypt text using AES algorithm
    public static String encrypt(String key, String text) {
        try {
            // Convert key from hexadecimal string to byte array
            byte[] keyBytes = hexStringToByteArray(key);
            // Create AES key specification using the key bytes
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Initialize cipher in encryption mode with AES algorithm, ECB mode, and PKCS5Padding and encryption key
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            // Encrypt the text bytes using the initialized cipher
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            // Convert encrypted bytes to hexadecimal string
            String hexEncrypted = bytesToHex(encryptedBytes);
            // Return the hexadecimal representation of the encrypted text
            return hexEncrypted;
        } catch (Exception e) {
            // Handle any exceptions that occur during encryption
            e.printStackTrace(); // Print the exception stack trace for debugging purposes
        }
        // If encryption fails, return null
        return null;
    }

    // Method to convert byte array to hexadecimal string
    public static String bytesToHex(byte[] bytes) {
        // Convert byte array to big integer
        BigInteger bigInt = new BigInteger(1, bytes);
        // Convert big integer to hexadecimal string
        String hexString = bigInt.toString(16);
        // Adjust length if necessary
        int paddingLength = (bytes.length * 2) - hexString.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hexString;
        } else {
            return hexString;
        }
    }

    // Method to convert hexadecimal string to byte array
    public static byte[] hexStringToByteArray(String s) {
        // Calculate the length of the byte array
        int len = s.length();
        // Create byte array to hold the converted data
        byte[] data = new byte[len / 2];
        // Iterate through the hexadecimal string and convert pairs of characters into bytes
        for (int i = 0; i < len; i += 2) {
            // Convert the pair of characters to bytes and store in the byte array
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        // Return the resulting byte array
        return data;
    }
}