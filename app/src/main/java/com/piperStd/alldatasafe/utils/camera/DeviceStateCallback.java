package com.piperStd.alldatasafe.utils.camera;

import android.hardware.camera2.CameraDevice;
import android.util.Log;

import androidx.annotation.NonNull;

public class DeviceStateCallback extends CameraDevice.StateCallback
{
    CameraHelper helper;
    public DeviceStateCallback(CameraHelper helper)
    {
        this.helper = helper;
    }
    @Override
    public void onOpened(@NonNull CameraDevice camera)
    {
        helper.device = camera;
        helper.createCaptureSession();
    }

    @Override
    public void onDisconnected(@NonNull CameraDevice camera)
    {
        helper.device.close();
        helper.device = null;
    }

    @Override
    public void onError(@NonNull CameraDevice camera, int error)
    {
        Log.e("Camera error", "Error " + error + " in camera with id " + camera.getId());

    }
}
