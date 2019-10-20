package com.piperStd.alldatasafe.utils.Others;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;

import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.R;

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

    public static void showException(Context context, String info)
    {
        Toast toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
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


    public static byte[] offsetToStartArray(byte[] arr, int offset)
    {
        if(offset < arr.length)
        {
            byte[] res = new byte[arr.length - offset];
            for (int i = 0; i < arr.length - offset; i++) {
                res[i] = arr[i + offset];
            }
            return res;
        }
        else
            return null;
    }

    public static void processService(int service, AppCompatImageView img)
    {
        switch (service) {
            case AuthServices.VK:
                img.setImageResource(R.drawable.ic_vk);
                break;
            case AuthServices.GITHUB:
                img.setImageResource(R.drawable.ic_github);
                break;
            case AuthServices.INSTAGRAM:
                img.setImageResource(R.drawable.ic_instagram);
                break;
            case AuthServices.STEAM:
                img.setImageResource(R.drawable.ic_steam);
                break;
            case AuthServices.FACEBOOK:
                img.setImageResource(R.drawable.ic_facebook);
                break;
            case AuthServices.SDO:
                img.setImageResource(R.drawable.ic_sdo);
                break;
            case AuthServices.UNKNOWN:
                img.setImageResource(R.drawable.ic_blower);
                break;

        }
    }

    public static byte processServiceChoice(int id, AppCompatImageView img)
    {
        switch(id) {
            case R.id.vk_item:
                img.setImageResource(R.drawable.ic_vk);
                return AuthServices.VK;
            case R.id.github_item:
                img.setImageResource(R.drawable.ic_github);
                return AuthServices.GITHUB;
            case R.id.instagram_item:
                img.setImageResource(R.drawable.ic_instagram);
                return AuthServices.INSTAGRAM;
            case R.id.steam_item:
                img.setImageResource(R.drawable.ic_steam);
                return AuthServices.STEAM;
            case R.id.facebook_item:
                img.setImageResource(R.drawable.ic_facebook);
                return AuthServices.FACEBOOK;
            case R.id.sdo_item:
                img.setImageResource(R.drawable.ic_sdo);
                return AuthServices.SDO;
            case R.id.other_item:
                img.setImageResource(R.drawable.ic_blower);
                return AuthServices.UNKNOWN;
            default:
                return -1;

        }
    }
}
