package com.piperStd.cryptosaver.utils;

import android.util.Log;

import java.nio.charset.StandardCharsets;

public class tools
{
    public static byte[] toBytes(String str)
    {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static void showException(Object obj, String info)
    {
        Log.e(obj.getClass().getName(), info);
    }
}
