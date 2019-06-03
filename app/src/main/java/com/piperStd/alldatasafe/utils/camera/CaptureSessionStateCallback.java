package com.piperStd.alldatasafe.utils.camera;

import android.content.Context;
import android.hardware.camera2.CameraCaptureSession;

import static com.piperStd.alldatasafe.utils.tools.showException;

public class CaptureSessionStateCallback extends CameraCaptureSession.StateCallback
{
    CameraHelper cameraHelper;

    public CaptureSessionStateCallback(CameraHelper cameraHelper)
    {
            this.cameraHelper = cameraHelper;
    }

    @Override
    public void onConfigured(CameraCaptureSession session)
    {
        cameraHelper.session = session;
        try
        {
            session.setRepeatingRequest(cameraHelper.builder.build(), null, null);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    @Override
    public void onConfigureFailed(CameraCaptureSession session)
    {

    }
}
