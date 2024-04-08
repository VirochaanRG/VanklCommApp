package com.example.vanklcommapp.KDC;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/*
* Performs the decryption functionalities taking in a key and text.
*/

public class Decrypter {

    // Constructor
    public Decrypter() {
        // Default constructor
    }

    // Method to decrypt text using AES algorithm
    public static String decrypt(String key, String text) {
        try {
            // Convert key from hexadecimal string to byte array
            byte[] keyBytes = hexStringToByteArray(key);
            // Create AES key specification using the key bytes
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Initialize cipher in decryption mode with AES algorithm, ECB mode, and PKCS5Padding and secret Key
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            // Convert text from hexadecimal string to byte array
            byte[] textBytes = hexStringToByteArray(text);
            // Decrypt the byte array using the initialized cipher
            byte[] decryptedBytes = cipher.doFinal(textBytes);
            // Convert decrypted byte array to UTF-8 encoded string
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            // Return the decrypted text
            return decryptedText;
        } catch (Exception e) {
            // Handle any exceptions that occur during decryption
            e.printStackTrace();
        }
        // If decryption fails, return null
        return null;
    }

    // Method to convert hexadecimal string to byte array
    private static byte[] hexStringToByteArray(String s) {
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