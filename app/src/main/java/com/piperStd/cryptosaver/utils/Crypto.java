package com.piperStd.cryptosaver.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class Credentials
{
    public byte[] key;
    public byte[] iv;
}

public class Crypto {

    public String password;
    public byte[] data;
    private byte[] decrypted;
    private byte[] encrypted = null;
    public Credentials credentials = new Credentials();

    public Crypto(byte[] data, String password)
    {
        this.data = data;
        this.password = password;
    }

    private byte[] getSHA256(byte[] data)
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch(NoSuchAlgorithmException e)
        {
            Log.d("Cryptography exception", e.getMessage());
        }
        return md.digest(data);
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
            Log.e("Cryptography exception", e.getMessage());
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
            Log.e("Cryptography exception", e.getMessage());
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

    public byte[] genCredentialsArr()
    {
        byte[] res = new byte[credentials.key.length + credentials.iv.length];
        for(int i = 0; i < credentials.key.length; i++)
        {
            res[i] = credentials.key[i];
        }
        for(int i = credentials.key.length; i < credentials.key.length + credentials.iv.length; i++)
        {
            res[i] = credentials.iv[i - credentials.key.length];
        }
        return res;
    }


}

