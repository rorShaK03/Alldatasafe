package com.piperStd.alldatasafe.utils.camera;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

public class TextureListener implements TextureView.SurfaceTextureListener
{
    CameraHelper cameraHelper = null;
    public TextureListener(CameraHelper cameraHelper)
    {
        this.cameraHelper = cameraHelper;
    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height)
    {
        cameraHelper.couldBeOpened = true;
        cameraHelper.openCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height)
    {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture)
    {
        cameraHelper.couldBeOpened = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture texture)
    {

    }
}
