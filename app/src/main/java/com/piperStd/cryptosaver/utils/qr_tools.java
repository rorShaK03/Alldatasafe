package com.piperStd.cryptosaver.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class qr_tools
{
    public Bitmap genBarcode(byte[] data)
    {
        Bitmap bitmap = null;
        String base64encoded = Base64.encodeToString(data, Base64.DEFAULT);
        MultiFormatWriter writer = new MultiFormatWriter();
        try
        {
            BitMatrix matrix = writer.encode(base64encoded, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(matrix);
        }
        catch(WriterException e)
        {
            Log.e("QR-code error", e.getMessage());
        }
        return bitmap;
    }
}
