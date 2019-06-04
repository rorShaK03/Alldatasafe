package com.piperStd.alldatasafe.utils.Others;

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

    public static <T> boolean contains(T[] arr, T obj)
    {
        for(T item : arr)
        {
            if(item.equals(obj))
            {
                return true;
            }
        }
        return false;
    }

    public static byte[] expandArray(byte[] arr, int extra)
    {
        byte[] res = new byte[arr.length + extra];
        for(int i = 0; i < arr.length; i++)
        {
            res[i] = arr[i];
        }
        return res;
    }

    public static byte[] offsetToEndArray(byte[] arr, int n, int offset)
    {
        byte[] res = new byte[arr.length];
        for(int i = 0; i < n; i++)
        {
            res[i + offset] = arr[i];
        }
        return res;
    }
}
