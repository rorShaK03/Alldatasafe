package com.piperStd.alldatasafe.utils.Files;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.piperStd.alldatasafe.utils.Others.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class FileHelper
{
    private Context context;
    String ENC_FILENAME = "encrypted.dat";

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

    public boolean write_to_encrypted(String encrypted)
    {
        try
        {
            FileOutputStream outStream = context.openFileOutput(ENC_FILENAME, Context.MODE_PRIVATE);
            outStream.write(encrypted.getBytes());
            outStream.close();
            return true;
        }
        catch(Exception e)
        {
            tools.showException(context, e.getMessage());
            return false;
        }
    }

    public String read_from_encrypted()
    {
        try
        {
            StringBuilder encrypted = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new FileReader(context.getApplicationContext().getFilesDir() + "/" + ENC_FILENAME));
            while ((line = br.readLine()) != null) {
                encrypted.append(line);
            }
            br.close();
            return encrypted.toString();
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
            return null;
        }
    }
}
