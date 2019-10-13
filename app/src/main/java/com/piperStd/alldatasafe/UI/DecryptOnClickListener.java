package com.piperStd.alldatasafe.UI;

import android.util.Log;
import android.view.View;

import com.piperStd.alldatasafe.R;

public class DecryptOnClickListener implements View.OnClickListener
{
    @Override
    public void onClick(View view)
    {
        ActivityLauncher launcher = new ActivityLauncher(view.getContext());
        switch(view.getId())
        {
            case R.id.qr_card:
                launcher.launchQrDetectActivity();
                break;
                /*
            case R.id.text_card:
                launcher.launchTextDecodeActivity();
                break;
                */
            case R.id.internal_card:
                launcher.launchInternalDecodeActivity();
                break;
        }
    }
}
