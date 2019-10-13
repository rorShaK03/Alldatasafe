package com.piperStd.alldatasafe.utils.Cryptographics;


import android.util.Base64;

import com.piperStd.alldatasafe.utils.Others.tools;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;



public class Crypto {


    private static final byte BLOCK_LENGTH = 16;
    public String password;
    public byte[] data;
    public byte[] encrypted;
    public byte[] decrypted;
    public boolean valid;
    private Credentials credentials;

    public Crypto(byte[] data, String password)
    {
        this.data = data;
        this.password = password;
        credentials = new Credentials();
    }

    public Crypto(byte[] data)
    {
        this.data = data;
        credentials = new Credentials();
    }


    public Crypto(byte[] data, byte[] iv)
    {
        this.data = data;
        credentials = new Credentials();
        credentials.iv = iv;
    }

    // Создание SHA-256 хеша по массиву байт
    private static byte[] getSHA256(byte[] data)
    {
        MessageDigest md;
        byte[] hash = null;
        try
        {
            md = MessageDigest.getInstance("SHA-256");
            hash = md.digest(data);
        }
        catch(Exception e)
        {
            showException(Crypto.class.getName(), "Couldn`t get SHA-256 hash: " + e.getMessage());
        }
        return hash;
    }

    // Генерация случайного 256-битного ключа
    public static byte[] keygen256()
    {
        byte[] key = new byte[32];
        HighEntropyRandom.genRandomFromUrandom(key);
        return key;
    }

    // Шифрование и запись шированного массива байт и вектора инициализации
    private void AES256CBC_encrypt()
    {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            SecretKey key = new SecretKeySpec(credentials.key, 0, credentials.key.length, "AES256");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(data);
            credentials.iv = cipher.getIV();
        }
        catch(Exception e)
        {
            showException(this, "Couldn`t encrypt message with AES-256: " + e.getMessage());
        }
    }

    // Расшифровка
    private boolean AES256CBC_decrypt()
    {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            SecretKey key = new SecretKeySpec(credentials.key, 0, credentials.key.length, "AES256");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(credentials.iv));
            decrypted = cipher.doFinal(data);
        }
        catch(Exception e)
        {
            showException(this, "Couldn`t decrypt message with AES-256: " + e.getMessage());
            return false;
        }
        return true;
    }

    // Получение ключа из пароля
    public static byte[] getKDF(String pass)
    {
        byte[] passBytes = tools.toBytes(pass);
        return getSHA256(passBytes);
    }

    // Инициализация ключа и шифрование
    public byte[] encrypt()
    {
        credentials.key = getKDF(password);
        AES256CBC_encrypt();
        data = encrypted;
        return data;
    }

    public byte[] encrypt(byte[] key)
    {
        credentials.key = key;
        AES256CBC_encrypt();
        data = encrypted;
        return data;
    }

    // Инициализация ключа и расшифровка
    public byte[] decrypt()
    {
        credentials.key = getKDF(password);
        if(AES256CBC_decrypt())
        {
            valid = true;
            data = decrypted;
        }
        else
        {
            valid = false;
            data = null;
        }
        return data;
    }

    public byte[] decrypt(byte[] key)
    {
        credentials.key = key;
        if(AES256CBC_decrypt())
        {
            valid = true;
            data = decrypted;
        }
        else
        {
            valid = false;
            data = null;
        }
        return data;
    }

    // Создание зашифрованного массива байт из encrypted и вектора инициализации
    public byte[] genEncryptedDataArr()
    {
        byte[] res = new byte[data.length + BLOCK_LENGTH];
        for(int i = 0; i < BLOCK_LENGTH; i++)
        {
            res[i] = credentials.iv[i];
        }
        for(int i = 0; i < data.length; i++)
        {
            res[i + BLOCK_LENGTH] = data[i];
        }
        return res;
    }

    // Создание объекта Crypto из зашифрованного массива байт
    private static Crypto parseEncryptedFormat(byte[] encData)
    {
        byte[] iv = new byte[BLOCK_LENGTH];
        byte[] encrypted = new byte[encData.length - BLOCK_LENGTH];
        for (int i = 0; i < BLOCK_LENGTH; i++)
        {
            iv[i] = encData[i];
        }
        for (int i = 0; i < encData.length - BLOCK_LENGTH; i++)
        {
            encrypted[i] = encData[i + BLOCK_LENGTH];
        }
        return new Crypto(encrypted, iv);

    }

    // Получение объекта Crypto из base64 строки
    public static Crypto parseBase64Encrypted(String base64)
    {

        return parseEncryptedFormat(Base64.decode(base64, Base64.DEFAULT));
    }

    // Получение строки base64 из объекта Crypto
    public String genBase64FromEncryptedData()
    {
        return Base64.encodeToString(genEncryptedDataArr(), Base64.DEFAULT);
    }

}

