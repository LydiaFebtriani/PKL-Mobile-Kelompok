package com.pab.unpar.pklmobilekelompok;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encoder {

    public synchronized String encrypt(String plaintext) throws Exception {

        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e.getMessage());
        }

        try {
            messageDigest.update(plaintext.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new Exception(e.getMessage());
        }

        byte[] raw = messageDigest.digest();
        String hash = Base64.encodeToString(raw, Base64.DEFAULT);
        return hash;
        
    }



}
