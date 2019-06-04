package com.piperStd.alldatasafe.utils.camera;

import android.hardware.camera2.CameraCaptureSession;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class CaptureSessionStateCallback extends CameraCaptureSession.StateCallback
{
    CameraHelper helper;

    public CaptureSessionStateCallback(CameraHelper helper)
    {
            this.helper = helper;
    }

    @Override
    public void onConfigured(CameraCaptureSession session)
    {
        helper.session = session;
        try
        {
            session.setRepeatingRequest(helper.builder.build(), null, null);
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
