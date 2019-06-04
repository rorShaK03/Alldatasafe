package com.piperStd.alldatasafe.utils.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.piperStd.alldatasafe.qr_detect_activity;
import com.piperStd.alldatasafe.utils.AsyncTools.AsyncHandlerThread;

import java.util.Arrays;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class CameraHelper
{
    public ImageReader reader;
    AsyncHandlerThread handlerThread;
    public boolean couldBeOpened = false;
    public CameraCaptureSession session;
    public CaptureRequest.Builder builder;
    public String text = null;
    private CameraManager manager = null;
    public CameraDevice device = null;
    private String cameraID;
    private Size maxJpegSize;
    private TextureView textureView = null;
    public qr_detect_activity context;


    public CameraHelper(qr_detect_activity context, TextureView view)
    {
        this.context = context;
        handlerThread = new AsyncHandlerThread("imageProcessor");
        handlerThread.start();
        handlerThread.prepareHandler();
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        cameraID = setUpCameraId();
        maxJpegSize = getMaxJpegSize();
        setTextureView(view);
        textureView.setSurfaceTextureListener(new TextureListener(this));
        reader = ImageReader.newInstance(1920, 1080, ImageFormat.YUV_420_888, 2);
        reader.setOnImageAvailableListener(new ImageAvailableListener(this), handlerThread.handler);
    }




    public void createCaptureSession()
    {
        Surface surface = reader.getSurface();
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(1280, 720);
        Surface previewSurface = new Surface(texture);
        try
        {
            builder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);
            builder.addTarget(previewSurface);
            device.createCaptureSession(Arrays.asList(surface, previewSurface), new CaptureSessionStateCallback(this), null);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    private void setTextureView(TextureView view)
    {
        textureView = view;
    }

    public boolean isOpen()
    {
        return (device != null);
    }

    private String setUpCameraId()
    {
        try
        {
            for(String cameraID : manager.getCameraIdList())
            {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraID);
                int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if(facing == CameraCharacteristics.LENS_FACING_BACK)
                {
                    return cameraID;
                }
            }
        }
        catch(Exception e)
        {
            showException(this, "Unable to set camera ID");
        }
        return null;
    }

    private Size getMaxJpegSize()
    {
        Size sz = null;
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraID);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = map.getOutputSizes(ImageFormat.JPEG);
            if(sizes != null)
            {
                sz = sizes[0];
            }
        }
        catch(Exception e)
        {
            showException(this, "Couldn`t get characteristics");
        }
        return sz;
    }

    public void openCamera()
    {
        try
        {
            manager.openCamera(cameraID, new DeviceStateCallback(this), null);
        }
        catch(SecurityException e)
        {
            showException(this, e.getMessage());
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void closeCamera()
    {
        device.close();
        device = null;
    }

}
