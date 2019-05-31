package com.piperStd.alldatasafe.utils;

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

    public static void showException(String tag, String info)
    {
        Log.e(tag, info);
    }

    static <T> boolean contains(T[] arr, T item)
    {
        for(int i = 0; i < arr.length; i++)
        {
            if(arr[i].equals(item))
            {
                return true;
            }
        }
        return false;
    }
}
