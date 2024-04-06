package com.example.vanklcommapp.KDC;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Decrypter {
    public Decrypter() {
    }
    public static String decrypt(String key, String text){
        try{
            byte[] keyBytes = hexStringToByteArray(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] textBytes = hexStringToByteArray(text);
            byte[] decryptedBytes = cipher.doFinal(textBytes);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            return decryptedText;
        }
        catch(Exception e){

        }
        return null;
    }
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
