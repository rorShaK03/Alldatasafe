package com.piperStd.alldatasafe.utils.camera;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;

public class CaptureSessionCaptureCallback extends CameraCaptureSession.CaptureCallback
{
    @Override
    public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult result)
    {
        super.onCaptureProgressed(session, request, result);
    }

    @Override
    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result)
    {
        super.onCaptureCompleted(session, request, result);
    }
}
