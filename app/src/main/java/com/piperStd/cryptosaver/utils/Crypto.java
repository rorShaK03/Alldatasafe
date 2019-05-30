package com.piperStd.cryptosaver.utils;


import android.util.Base64;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.piperStd.cryptosaver.utils.tools.showException;



public class Crypto {

    public String password;
    public byte[] data;
    private byte[] decrypted = null;
    private byte[] encrypted = null;
    private Credentials credentials;

    public Crypto(byte[] data, String password)
    {
        this.data = data;
        this.password = password;
        credentials = new Credentials();
    }

    private byte[] getSHA256(byte[] data)
    {
        MessageDigest md = null;
        byte[] hash = null;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(data);
        }
        catch(Exception e)
        {
            showException(this, "Couldn`t get SHA-256 hash: " + e.getMessage());
        }
        return hash;
    }

    private void AES256CBC_encrypt()
    {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            SecretKey key = new SecretKeySpec(credentials.key, 0, credentials.key.length, "AES256");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(decrypted);
            credentials.iv = cipher.getIV();
        }
        catch(Exception e)
        {
            showException(this, "Couldn`t encrypt message with AES-256: " + e.getMessage());
        }
    }

    private void AES256CBC_decrypt()
    {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            SecretKey key = new SecretKeySpec(credentials.key, 0, credentials.key.length, "AES256");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(credentials.iv));
            decrypted = cipher.doFinal(encrypted);
        }
        catch(Exception e)
        {
            showException(this, "Couldn`t decrypt message with AES-256: " + e.getMessage());
        }
    }

    public void KDF(String pass)
    {
        byte[] passBytes = tools.toBytes(pass);
        credentials.key = getSHA256(passBytes);
    }

    public byte[] encrypt()
    {
        decrypted = data;
        KDF(password);
        AES256CBC_encrypt();
        return encrypted;
    }

    public byte[] decrypt(byte[] data, String password)
    {
        encrypted = data;
        KDF(password);
        AES256CBC_decrypt();
        return decrypted;
    }

    public byte[] genEncryptedDataArr()
    {
        byte[] res = new byte[encrypted.length + credentials.iv.length];
        for(int i = 0; i < credentials.iv.length; i++)
        {
            res[i] = credentials.iv[i];
        }
        for(int i = credentials.iv.length; i < credentials.iv.length + encrypted.length; i++)
        {
            res[i] = encrypted[i - credentials.iv.length];
        }
        return res;
    }

    public String genBase64FromEncryptedData()
    {
        return Base64.encodeToString(genEncryptedDataArr(), Base64.DEFAULT);
    }

    public Credentials getCredentials()
    {
        return credentials;
    }
}

