package com.piperStd.alldatasafe.Core;

import android.util.Log;

import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AuthNode
{
    public static String typeName = "alldatasafe/auth";
    public String login;
    public String password;
    public byte service;
    private static byte separator = 0x00;
    private byte[] data;

    public AuthNode(byte[] data)
    {
        this.data = data;
        service = data[0];
        ByteArrayOutputStream loginBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream passwordBytes = new ByteArrayOutputStream();
        int i = 0;
        while(i < data.length && data[i] != separator)
        {
            loginBytes.write(data[i]);
            i++;
        }
        while(i < data.length)
        {
            passwordBytes.write(data[i]);
            i++;
        }
        this.password = new String(passwordBytes.toByteArray(), StandardCharsets.UTF_8);
        this.login = new String(loginBytes.toByteArray(), StandardCharsets.UTF_8);
    }

    public AuthNode(byte service, String login, String password)
    {
        this.login = login;
        this.password = password;
        this.service = service;
        byte[] loginBytes = login.getBytes(StandardCharsets.UTF_8);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        this.data = new byte[loginBytes.length + passwordBytes.length + 2];
        this.data[0] = service;
        Log.d("data_length", Integer.toString(data.length));
        for(int i = 1; i <= loginBytes.length; i++)
        {
            data[i] = loginBytes[i - 1];
        }
        Log.d("data_length", Integer.toString(data.length));
        data[loginBytes.length + 1] = separator;
        for(int i = 1; i <= passwordBytes.length; i++)
        {
            data[i + loginBytes.length + 1] = passwordBytes[i - 1];
        }
        Log.d("data_length", Integer.toString(data.length));
    }

    public String getEncryptedString(String password)
    {
        byte[] typeNameBytes = typeName.getBytes(StandardCharsets.UTF_8);
        byte[] finalData = new byte[data.length + typeNameBytes.length + 1];
        for(int i = 0; i < typeNameBytes.length; i++)
            finalData[i] = typeNameBytes[i];

        finalData[typeNameBytes.length] = this.separator;

        for(int i = 0; i < data.length; i++)
            finalData[i + typeNameBytes.length + 1] = data[i];
        Log.d("finalData", new String(finalData));
        Crypto crypto = new Crypto(finalData, password);
        crypto.encrypt();
        return crypto.genBase64FromEncryptedData();
    }

    public static AuthNode DecryptAndParse(String encrypted, String password)
    {
        Crypto crypto = Crypto.parseBase64Encrypted(encrypted);
        crypto.password = password;
        crypto.decrypt();
        byte[] decrypted = crypto.data;
        byte[] typeBytes = new byte[typeName.length()];
        int i = 0;
        while(i < decrypted.length && decrypted[i] != separator)
        {
            typeBytes[i] = decrypted[i];
            i++;
        }
        Log.d("type", new String(typeBytes, StandardCharsets.UTF_8));
        if(new String(typeBytes, StandardCharsets.UTF_8).equals(typeName))
        {
            Log.d("state", "true");
            byte[] dataBytes = new byte[decrypted.length - typeName.length() - 1];
            for(int j = 0; j < dataBytes.length; j++)
            {
                dataBytes[j] = decrypted[i + j + 1];
            }
            return new AuthNode(dataBytes);
        }
        else
            return null;
    }
}
