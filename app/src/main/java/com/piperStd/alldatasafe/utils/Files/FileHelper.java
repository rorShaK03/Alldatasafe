package com.piperStd.alldatasafe.utils.Files;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class FileHelper
{
    private Context context;

    public FileHelper(Context context)
    {
        this.context = context;
    }

    public Uri saveFileInternal(ByteArrayOutputStream stream, String filename)
    {
        File cachePath = new File(context.getCacheDir(), "images");
        cachePath.mkdir();
        Log.d("file", cachePath.getAbsolutePath());
        FileOutputStream outStream;
        try
        {
            outStream = new FileOutputStream(cachePath + "/image.jpg");
            outStream.write(stream.toByteArray());
            outStream.close();

        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
        return FileProvider.getUriForFile(context, "com.piperStd.alldatasafe.fileprovider", new File(cachePath, "image.jpg"));
    }
}
