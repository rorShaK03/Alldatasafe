package com.piperStd.alldatasafe.utils.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.ImageReader;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class ImageAvailableListener implements ImageReader.OnImageAvailableListener
{
    public CameraHelper helper;
    public ImageAvailableListener(CameraHelper helper)
    {
        this.helper = helper;
    }
    @Override
    public void onImageAvailable(ImageReader reader)
    {
        Image image = reader.acquireLatestImage();
        if(image == null) return;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteBuffer bufferY = image.getPlanes()[0].getBuffer();
        ByteBuffer bufferU = image.getPlanes()[1].getBuffer();
        ByteBuffer bufferV = image.getPlanes()[2].getBuffer();
        byte[] dataY = new byte[bufferY.capacity()];
        byte[] dataU = new byte[bufferU.capacity()];
        byte[] dataV = new byte[bufferV.capacity()];
        bufferY.get(dataY);
        bufferU.get(dataU);
        bufferV.get(dataV);
        try
        {
            outputStream.write(dataY);
            outputStream.write(dataV);
            outputStream.write(dataU);

        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
        YuvImage yuv = new YuvImage(outputStream.toByteArray(), ImageFormat.NV21, 1920, 1080, null);
        yuv.compressToJpeg(new Rect(0, 0, 1920, 1080), 95, bitmapStream);
        byte[] imgByteArr = bitmapStream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByteArr, 0, imgByteArr.length);
        helper.context.handler.obtainMessage(0, bitmap).sendToTarget();
        image.close();

    }

}
