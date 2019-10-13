package com.piperStd.alldatasafe.Core;

import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;
import com.piperStd.alldatasafe.utils.Others.tools;

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

    // Создание объекта AuthNode из расшифрованного массива байт
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

    // Создание объекта AuthNode по логину, паролю и сервису и генерация соответствующего массива байт
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

    /*
    public String getEncryptedString(String password)
    {
        byte[] type_name_bytes = typeName.getBytes(StandardCharsets.UTF_8);
        byte[] final_data = new byte[data.length + type_name_bytes.length + 1];
        for(int i = 0; i < type_name_bytes.length; i++)
            final_data[i] = type_name_bytes[i];

        final_data[type_name_bytes.length] = separator;

        for(int i = 0; i < data.length; i++)
            final_data[i + type_name_bytes.length + 1] = data[i];
        Crypto crypto = new Crypto(final_data, password);
        crypto.encrypt();
        return crypto.genBase64FromEncryptedData();
    }

    public String getEncryptedString(byte[] key)
    {
        byte[] type_name_bytes = typeName.getBytes(StandardCharsets.UTF_8);
        byte[] final_data = new byte[data.length + type_name_bytes.length + 1];
        for(int i = 0; i < type_name_bytes.length; i++)
            final_data[i] = type_name_bytes[i];

        final_data[type_name_bytes.length] = separator;

        for(int i = 0; i < data.length; i++)
            final_data[i + type_name_bytes.length + 1] = data[i];
        Crypto crypto = new Crypto(final_data);
        crypto.encrypt(key);
        return crypto.genBase64FromEncryptedData();
    }
    */

    // Создание шифрованного массива байт по массиву объектов AuthNode
    public static String getEncryptedStringFromArray(AuthNode[] arr, byte[] key)
    {
        byte[] type_name_bytes = typeName.getBytes(StandardCharsets.UTF_8);
        int length = type_name_bytes.length;
        for(int i = 0; i < arr.length; i++)
            length += arr[i].data.length + 1;
        length++;
        byte[] final_data = new byte[length];
        final_data[0] = (byte)arr.length;
        for(int i = 1; i < type_name_bytes.length + 1; i++)
            final_data[i] = type_name_bytes[i - 1];

        final_data[type_name_bytes.length + 1] = separator;
        int counter = 0;
        for(int j = 0; j < arr.length; j++)
        {
            for (int i = 0; i < arr[j].data.length; i++)
            {
                final_data[counter + type_name_bytes.length + 2] = arr[j].data[i];
                counter++;
            }
            if(j != arr.length - 1)
            {
                final_data[counter + type_name_bytes.length + 2] = nodes_separator;
                counter++;
            }
        }
        Crypto crypto = new Crypto(final_data);
        crypto.encrypt(key);
        return crypto.genBase64FromEncryptedData();
    }

    // Получение массива AuthNode по шифрованному массиву байт
    public static AuthNode[] DecryptAndParseArray(String encrypted, byte[] key)
    {
        Crypto crypto = Crypto.parseBase64Encrypted(encrypted);
        byte[] decrypted = crypto.decrypt(key);
        if(decrypted == null)
            return null;
        byte[] typeBytes = new byte[typeName.length()];
        byte length = decrypted[0];
        decrypted = tools.offsetToStartArray(decrypted, 1);
        AuthNode[] nodes = new AuthNode[length];
        int i = 0;
        while(i < typeName.length() && decrypted[i] != separator)
        {
            typeBytes[i] = decrypted[i];
            i++;
        }
        if(new String(typeBytes, StandardCharsets.UTF_8).equals(typeName))
        {
            decrypted = tools.offsetToStartArray(decrypted, typeName.length() + 1);
            for(int j = 0; j < length; j++)
            {
                int k = 0;
                while(k  < decrypted.length && decrypted[k] != nodes_separator)
                    k++;
                byte[] node = new byte[k];
                for(int l = 0; l < k; l++)
                {
                    node[l] = decrypted[l];
                }
                decrypted = tools.offsetToStartArray(decrypted,k + 1);
                nodes[j] = new AuthNode(node);
            }
            return nodes;
        }
        else
            return null;
    }

    /*
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
        */

}
