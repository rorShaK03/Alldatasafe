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
    private static byte nodes_separator = 0x01;
    private byte[] data;

    public AuthNode(byte[] data)
    {
        this.data = data;
        service = data[0];
        ByteArrayOutputStream loginBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream passwordBytes = new ByteArrayOutputStream();
        int i = 1;
        while(i < data.length && data[i] != separator)
        {
            loginBytes.write(data[i]);
            i++;
        }
        i++;
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
        for(int i = 1; i <= loginBytes.length; i++)
        {
            data[i] = loginBytes[i - 1];
        }
        data[loginBytes.length + 1] = separator;
        for(int i = 1; i <= passwordBytes.length; i++)
        {
            data[i + loginBytes.length + 1] = passwordBytes[i - 1];
        }
    }

    public String getEncryptedString(String password)
    {
        byte[] typeNameBytes = typeName.getBytes(StandardCharsets.UTF_8);
        byte[] finalData = new byte[data.length + typeNameBytes.length + 1];
        for(int i = 0; i < typeNameBytes.length; i++)
            finalData[i] = typeNameBytes[i];

        finalData[typeNameBytes.length] = separator;

        for(int i = 0; i < data.length; i++)
            finalData[i + typeNameBytes.length + 1] = data[i];
        Crypto crypto = new Crypto(finalData, password);
        crypto.encrypt();
        return crypto.genBase64FromEncryptedData();
    }

    public String getEncryptedString(byte[] key)
    {
        byte[] typeNameBytes = typeName.getBytes(StandardCharsets.UTF_8);
        byte[] finalData = new byte[data.length + typeNameBytes.length + 1];
        for(int i = 0; i < typeNameBytes.length; i++)
            finalData[i] = typeNameBytes[i];

        finalData[typeNameBytes.length] = separator;

        for(int i = 0; i < data.length; i++)
            finalData[i + typeNameBytes.length + 1] = data[i];
        Crypto crypto = new Crypto(finalData);
        crypto.encrypt(key);
        return crypto.genBase64FromEncryptedData();
    }

    public static String getEncryptedStringFromArray(AuthNode[] arr, byte[] key)
    {
        byte[] typeNameBytes = typeName.getBytes(StandardCharsets.UTF_8);
        int length = typeNameBytes.length;
        for(int i = 0; i < arr.length; i++)
            length += arr[i].data.length + 1;
        length++;
        byte[] finalData = new byte[length];
        finalData[0] = (byte)arr.length;
        for(int i = 1; i < typeNameBytes.length + 1; i++)
            finalData[i] = typeNameBytes[i - 1];

        finalData[typeNameBytes.length + 1] = separator;
        int counter = 0;
        for(int j = 0; j < arr.length; j++)
        {
            for (int i = 0; i < arr[j].data.length; i++)
            {
                finalData[counter + typeNameBytes.length + 2] = arr[j].data[i];
                counter++;
            }
            if(j != arr.length - 1)
            {
                finalData[counter + typeNameBytes.length + 2] = nodes_separator;
                counter++;
            }
        }
        Crypto crypto = new Crypto(finalData);
        crypto.encrypt(key);
        return crypto.genBase64FromEncryptedData();
    }

    public static AuthNode[] DecryptAndParseArray(String encrypted, byte[] key)
    {
        Crypto crypto = Crypto.parseBase64Encrypted(encrypted);
        byte[] decrypted = crypto.decrypt(key);
        if(decrypted == null)
            return null;
        byte[] typeBytes = new byte[typeName.length()];
        byte length = decrypted[0];
        AuthNode[] nodes = new AuthNode[length];
        int i = 1;
        while(i < typeName.length() + 1 && decrypted[i] != separator)
        {
            typeBytes[i - 1] = decrypted[i];
            i++;
        }
        if(new String(typeBytes, StandardCharsets.UTF_8).equals(typeName))
        {
            int counter = 0;
            for(int j = 0; j < length; j++)
            {
                int k = 0;
                while(k + typeName.length() + 2 < decrypted.length && decrypted[k + typeName.length() + 2] != nodes_separator)
                    k++;
                byte[] node = new byte[k];
                for(int l = 0; l < k; l++)
                {
                    node[l] = decrypted[counter + l + typeName.length() + 2];
                }
                counter += k + 1;
                nodes[j] = new AuthNode(node);
            }
            return nodes;
        }
        else
            return null;
    }

    public static AuthNode DecryptAndParse(String encrypted, String password)
    {
        Crypto crypto = Crypto.parseBase64Encrypted(encrypted);
        crypto.password = password;
        byte[] decrypted = crypto.decrypt();
        if(decrypted == null)
            return null;
        byte[] typeBytes = new byte[typeName.length()];
        int i = 0;
        while(i < typeName.length() && decrypted[i] != separator)
        {
            typeBytes[i] = decrypted[i];
            i++;
        }
        if(new String(typeBytes, StandardCharsets.UTF_8).equals(typeName))
        {
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

    public static AuthNode DecryptAndParse(String encrypted, byte[] key)
    {
        Crypto crypto = Crypto.parseBase64Encrypted(encrypted);
        byte[] decrypted = crypto.decrypt(key);
        if(decrypted == null)
            return null;
        byte[] typeBytes = new byte[typeName.length()];
        int i = 0;
        while(i < typeName.length() && decrypted[i] != separator)
        {
            typeBytes[i] = decrypted[i];
            i++;
        }
        if(new String(typeBytes, StandardCharsets.UTF_8).equals(typeName))
        {
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
