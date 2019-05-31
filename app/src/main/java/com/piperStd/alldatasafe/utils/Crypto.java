package com.piperStd.alldatasafe.utils;


import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.piperStd.alldatasafe.utils.tools.showException;



public class Crypto {

    public final byte[] salt = {};

    public String password;
    public byte[] data;
    private Credentials credentials;

    public Crypto()
    {
        credentials = new Credentials();
    }

    public Crypto(byte[] data, String password)
    {
        this.data = data;
        this.password = password;
        credentials = new Credentials();
    }

    public Crypto(byte[] data, String password, byte[] iv)
    {
        this.data = data;
        this.password = password;
        credentials = new Credentials();
        credentials.iv = iv;
    }

    public Crypto(byte[] data, byte[] iv)
    {
        this.data = data;
        credentials = new Credentials();
        credentials.iv = iv;
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
            data = cipher.doFinal(data);
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
            data = cipher.doFinal(data);
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
        KDF(password);
        AES256CBC_encrypt();
        return data;
    }

    public byte[] decrypt()
    {
        KDF(password);
        AES256CBC_decrypt();
        Log.d("decrypted", new String(data));
        return data;
    }

    public byte[] genEncryptedDataArr()
    {
        byte[] res = new byte[data.length + credentials.iv.length];
        for(int i = 0; i < credentials.iv.length; i++)
        {
            res[i] = credentials.iv[i];
        }
        for(int i = 0; i < data.length; i++)
        {
            res[i + credentials.iv.length] = data[i];
        }
        return res;
    }

    private static Crypto parseEncryptedFormat(byte[] encData)
    {
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[encData.length - 16];
        for(int i = 0; i < 16; i++)
        {
            iv[i] = encData[i];
        }
        for(int i = 0; i < encData.length - 16; i++)
        {
            encrypted[i] = encData[i + 16];
        }
        return new Crypto(encrypted, iv);
    }

    public static Crypto parseBase64Encrypted(String base64)
    {

        return parseEncryptedFormat(Base64.decode(base64, Base64.DEFAULT));
    }

    public String genBase64FromEncryptedData()
    {
        return Base64.encodeToString(genEncryptedDataArr(), Base64.DEFAULT);
    }

    public String genStringFromDecrypted()
    {
        return new String(data);
    }

    public Credentials getCredentials()
    {
        return credentials;
    }
}

