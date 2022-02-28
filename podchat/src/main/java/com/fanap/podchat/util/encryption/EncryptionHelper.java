package com.fanap.podchat.util.encryption;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionHelper {
    KeyGenerator keyGenerator;
    SecretKey secretKey;
    byte[] IV = new byte[16];
    SecureRandom random;

    private void prepareSecretKey() {
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            secretKey = keyGenerator.generateKey();
            random = new SecureRandom();
            random.nextBytes(IV);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public String getStringSecretKey() {
        prepareSecretKey();
        SecretKeySpec mAesKey = new SecretKeySpec(secretKey.getEncoded(), "AES");
        return Base64.encodeToString(mAesKey.getEncoded(), Base64.DEFAULT);
    }

    public SecretKey convertStringSecretKey(String mAesKey_string) {
        byte[] aesByte = Base64.decode(mAesKey_string, Base64.DEFAULT);
        return new SecretKeySpec(aesByte, "AES");
    }


    private PrivateKey getPrivateFromString(String privatekey) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            keySpecPKCS8 = new PKCS8EncodedKeySpec(decodeInApi26AndHigher(privatekey));
        } else
            keySpecPKCS8 = new PKCS8EncodedKeySpec(decodeTillApi26(privatekey));
        return kf.generatePrivate(keySpecPKCS8);
    }


    public String decrypt(String encryptedMessage, String privatekey) throws Exception {
        byte[] encryptedBytes;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encryptedBytes = decodeInApi26AndHigher(encryptedMessage);
        } else
            encryptedBytes = decodeTillApi26(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA");
        PrivateKey privateKey = getPrivateFromString(privatekey);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return new String(decryptedMessage, StandardCharsets.UTF_8);
        } else
            return new String(decryptedMessage);
    }

    private byte[] decodeTillApi26(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private byte[] decodeInApi26AndHigher(String data) {
        return java.util.Base64.getDecoder().decode(data);
    }


}
