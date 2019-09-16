package com.piperStd.alldatasafe.UI;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.piperStd.alldatasafe.crypt_activity;
import com.piperStd.alldatasafe.internal_decode_activity;
import com.piperStd.alldatasafe.internal_show_activity;
import com.piperStd.alldatasafe.nfc_decode_activity;
import com.piperStd.alldatasafe.nfc_write_activity;
import com.piperStd.alldatasafe.qr_detect_activity;
import com.piperStd.alldatasafe.qr_show_activity;
import com.piperStd.alldatasafe.decrypt_activity;
import com.piperStd.alldatasafe.text_decode_activity;
import com.piperStd.alldatasafe.text_show_activity;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class ActivityLauncher
{
    private Context context;
    public ActivityLauncher(Context context)
    {
        this.context = context;
    }

    public void launchQRCodeActivity(String encrypted)
    {
        try {
            Intent intent = new Intent(context, qr_show_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("ENCRYPTED", encrypted);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchTextShowActivity(String encrypted)
    {
        try {
            Intent intent = new Intent(context, text_show_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("ENCRYPTED", encrypted);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchInternalShowActivity(String encrypted)
    {
        try {
            Intent intent = new Intent(context, internal_show_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("ENCRYPTED", encrypted);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchNFCActivity(String encrypted)
    {
        try {
            Intent intent = new Intent(context, nfc_write_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra("ENCRYPTED", encrypted);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchKeygenActivity()
    {
        try {
            if (context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF)) {
                Intent intent = new Intent(context, nfc_write_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setAction("keygen");
                context.startActivity(intent);
            }
            else
            {
                Toast toast = Toast.makeText(this.context, "У вас нет NFC-модуля!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchNfcDecodeActivity()
    {
        try {
            Intent intent = new Intent(context, nfc_decode_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchCryptActivity()
    {
        try {
            Intent intent = new Intent(context, crypt_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchQrDetectActivity()
    {
        try {
            Intent intent = new Intent(context, qr_detect_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchTextDecodeActivity()
    {
        try {
            Intent intent = new Intent(context, text_decode_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchInternalDecodeActivity()
    {
        try {
            Intent intent = new Intent(context, internal_decode_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    public void launchDecryptActivity()
    {
        try {
            Intent intent = new Intent(context, decrypt_activity.class);
            context.startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }
}
