package com.piperStd.alldatasafe.utils.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.util.Arrays;

import static com.piperStd.alldatasafe.utils.tools.showException;

public class CameraHelper
{
    public CameraCaptureSession session;
    public CaptureRequest.Builder builder;
    private CameraManager manager = null;
    private CameraDevice device = null;
    private String cameraID;
    private Size maxJpegSize;
    private TextureView textureView = null;
    private Context context;


    public CameraHelper(Context context, TextureView view)
    {
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        cameraID = setUpCameraId();
        maxJpegSize = getMaxJpegSize();
        setTextureView(view);
        textureView.setSurfaceTextureListener(new TextureListener(this));

    }




    private CameraDevice.StateCallback deviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera)
        {
            device = camera;
            createPreviewSession();
            Log.d("Camera", "Camera with id: " + camera.getId() + " was opened");
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera)
        {
            device.close();
            Log.d("Camera", "Camera with id: " + camera.getId() + " was disconnected");
            device = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error)
        {
            Log.e("Camera error", "Error " + error + " in camera with id " + camera.getId());

        }
    };

    private void createPreviewSession()
    {
        Log.e("Camera", textureView == null ? "textureView == null" : "OK");
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(1920, 1080);
        Surface surface = new Surface(texture);
        try
        {
            builder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(surface);
            device.createCaptureSession(Arrays.asList(surface), new CaptureSessionStateCallback(this), null);
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
            showException(this, "Unable to set cameraHelper ID");
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
            manager.openCamera(cameraID, deviceStateCallback, null);
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
