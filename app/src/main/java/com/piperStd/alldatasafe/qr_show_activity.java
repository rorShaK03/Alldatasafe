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
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.Core.Text;
import com.piperStd.alldatasafe.UI.ActivityLauncher;
import com.piperStd.alldatasafe.UI.MainNavigationListener;
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
    MainNavigationListener navListener = null;
    NavigationView navigation = null;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        flipper = findViewById(R.id.viewFlipper);
        drawerLayout = findViewById(R.id.drawer_layout);
        navListener = new MainNavigationListener(this, drawerLayout);
        navigation = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigation.setNavigationItemSelectedListener(navListener);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        navigation.getMenu().getItem(0).setChecked(true);
        flipper.setDisplayedChild(1);
        findViewById(R.id.shareQrButton).setOnClickListener(this);
        qrImage = findViewById(R.id.imageView);
        try
        {
            Intent intent = getIntent();
            String encrypted = intent.getStringExtra("ENCRYPTED");
            barcode = QrHelper.genBarcode(encrypted);
            qrImage.setImageBitmap(barcode);
        }
        catch(Exception e)
        {
            showException(this, "Неизвестная ошибка создания QR-кода!");
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
