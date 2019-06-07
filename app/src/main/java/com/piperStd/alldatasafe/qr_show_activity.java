package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;
import com.piperStd.alldatasafe.utils.Detectors.QrHelper;
import com.piperStd.alldatasafe.utils.Files.FileHelper;
import com.piperStd.alldatasafe.utils.Others.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class qr_show_activity extends AppCompatActivity implements View.OnClickListener {

    ImageView qrImage;
    Bitmap barcode;
    ViewFlipper flipper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigation = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        flipper = findViewById(R.id.viewFlipper);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        flipper.setDisplayedChild(1);
        findViewById(R.id.shareQrButton).setOnClickListener(this);
        qrImage = findViewById(R.id.imageView);
        try
        {
            Bundle extras = getIntent().getExtras();
            String text = (String)extras.get("com.piperstd.alldatasafe.EXTRA_TEXT");
            String password = (String)extras.get("com.piperstd.alldatasafe.EXTRA_PASS");
            Crypto crypto = new Crypto(tools.toBytes(text), password);
            crypto.encrypt();
            barcode = QrHelper.genBarcode(crypto.genEncryptedDataArr());
            qrImage.setImageBitmap(barcode);
        }
        catch(Exception e)
        {
            showException(this, "Unable to start qr_show_activity: " + e.getMessage());
        }

    }

    @Override
    public void onClick(View view)
    {
        FileHelper fileHelper = new FileHelper(this);
        ByteArrayOutputStream fileOutStream = new ByteArrayOutputStream();
        barcode.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);
        Uri uri = fileHelper.saveFileInternal(fileOutStream, "barcode.jpg");
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType(getContentResolver().getType(uri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Отправить QR-код с помощью"));
    }


}
