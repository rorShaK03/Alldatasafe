package com.piperStd.alldatasafe.Core;

import android.util.Log;

import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;

import java.nio.charset.StandardCharsets;

public class Text
{
    public static String typeName = "alldatasafe/text";
    private static byte separator = 0x00;
    private String password;
    private byte[] data;

    public Text(String text)
    {
        this.data = text.getBytes(StandardCharsets.UTF_8);
    }

    public Text(byte[] data)
    {
        this.data = data;
    }


    public byte getTypeName()
    {
        return DataFormats.TEXT;
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

        Crypto crypto = new Crypto(finalData, password);
        crypto.encrypt();
        return crypto.genBase64FromEncryptedData();
    }

    public String getString()
    {
        return new String(data, StandardCharsets.UTF_8);
    }

    public static Text DecryptAndParse(String encrypted, String password)
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
           return new Text(dataBytes);
        }
        else
            return null;
    }
}
