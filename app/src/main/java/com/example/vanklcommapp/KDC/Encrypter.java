package com.example.vanklcommapp.KDC;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encrypter {
    public Encrypter() {
    }
    public static String encrypt(String key, String text){
        try{
            byte[] keyBytes = hexStringToByteArray(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());
            String hexEncrypted = bytesToHex(encryptedBytes);
            return hexEncrypted;
        }
        catch(Exception e){

        }
        return null;
    }
    public static String bytesToHex(byte[] bytes) {
        BigInteger bigInt = new BigInteger(1, bytes);
        String hexString = bigInt.toString(16);
        // Adjust length if necessary (prepend zeros)
        int paddingLength = (bytes.length * 2) - hexString.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hexString;
        } else {
            return hexString;
        }
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
