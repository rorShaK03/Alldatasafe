package com.piperStd.alldatasafe.utils.Detectors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class QrHelper
{
    public BarcodeDetector detector;
    public static Bitmap genBarcode(byte[] data)
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
            Log.e("QrHelper-code error", e.getMessage());
        }
        return bitmap;
    }

    public String readBarcode(Context context, Bitmap bitmap)
    {
        detector = new BarcodeDetector.Builder(context.getApplicationContext()).setBarcodeFormats(Barcode.QR_CODE).build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);
        if(barcodes.size() > 0)
        {
            Barcode barcode = barcodes.valueAt(0);
            return barcode.rawValue;
        }
        else
        {
            return null;
        }
    }
}
