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
    public String service_name = "";
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
        ByteArrayOutputStream serviceNameBytes = new ByteArrayOutputStream();
        int i = 1;
        if(service == AuthServices.UNKNOWN)
        {
            while(i < data.length && data[i] != separator)
            {
                serviceNameBytes.write(data[i]);
                i++;
            }
            i++;
        }
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
        this.service_name = new String(serviceNameBytes.toByteArray(), StandardCharsets.UTF_8);
    }

    // Создание объекта AuthNode по логину, паролю и сервису и генерация соответствующего массива байт
    public AuthNode(byte service, String login, String password)
    {
        this.login = login;
        this.password = password;
        this.service = service;
        ByteArrayOutputStream data_stream = new ByteArrayOutputStream();
        byte[] loginBytes = login.getBytes(StandardCharsets.UTF_8);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        this.data = new byte[loginBytes.length + passwordBytes.length + 2];
        try
        {
            data_stream.write(service);
            data_stream.write(loginBytes);
            data_stream.write(separator);
            data_stream.write(passwordBytes);
        }
        catch(Exception e)
        {
            this.data = null;
            return;
        }
        this.data = data_stream.toByteArray();
    }

    public AuthNode(String service_name, String login, String password)
    {
        this.login = login;
        this.password = password;
        this.service = AuthServices.UNKNOWN;
        this.service_name = service_name;
        byte[] loginBytes = login.getBytes(StandardCharsets.UTF_8);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] serviceNameBytes = service_name.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream data_stream = new ByteArrayOutputStream();
        try
        {
            data_stream.write(service);
            data_stream.write(serviceNameBytes);
            data_stream.write(separator);
            data_stream.write(loginBytes);
            data_stream.write(separator);
            data_stream.write(passwordBytes);
        }
        catch(Exception e)
        {
            this.data = null;
            return;
        }
        this.data = data_stream.toByteArray();
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
        ByteArrayOutputStream final_stream = new ByteArrayOutputStream();
        byte[] type_name_bytes = typeName.getBytes(StandardCharsets.UTF_8);
        try
        {
            final_stream.write((byte) arr.length);
            final_stream.write(type_name_bytes);
            final_stream.write(separator);
            for (int j = 0; j < arr.length; j++) {
                final_stream.write(arr[j].data);
                final_stream.write(nodes_separator);
            }
            Crypto crypto = new Crypto(final_stream.toByteArray());
            crypto.encrypt(key);
            return crypto.genBase64FromEncryptedData();
        }
        catch(Exception e)
        {
            return null;
        }
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
