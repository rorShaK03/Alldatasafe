package com.piperStd.alldatasafe.UI;

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
            case R.id.nfc_card:
                launcher.launchNfcDecodeActivity();
                break;
        }
    }
}
