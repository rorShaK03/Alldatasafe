package com.piperStd.alldatasafe.utils.AsyncTools;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class AsyncHandlerThread extends HandlerThread
{
    public Handler handler;

    public AsyncHandlerThread(String name)
    {
        super(name);
    }


    public void prepareHandler()
    {
        handler = new Handler(getLooper());
    }

}
