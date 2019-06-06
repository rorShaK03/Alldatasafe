package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.TextureView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;
import com.piperStd.alldatasafe.utils.Detectors.QrHelper;
import com.piperStd.alldatasafe.UI.ActivityLauncher;
import com.piperStd.alldatasafe.UI.MainNavigationListener;
import com.piperStd.alldatasafe.utils.camera.CameraHelper;

public class qr_detect_activity extends AppCompatActivity {

    final int CAMERA_PERMISSION_ID = 0;
    TextView textView;
    EditText edit;
    public Handler handler;
    QrHelper qrHelper;
    CameraHelper camera;
    ViewFlipper flipper = null;
    NavigationView navigation = null;
    AppCompatButton nextBtn;
    DrawerLayout drawerLayout;
    ActivityLauncher launcher = null;
    MainNavigationListener navListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        launcher = new ActivityLauncher(this);
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_ID);
        }
        else
        {
            camera = new CameraHelper(this, (TextureView) findViewById(R.id.camera_preview));
        }
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onStart()
    {
        super.onStart();
        flipper.setDisplayedChild(4);
        textView = findViewById(R.id.qr_detect_textView);
        edit = findViewById(R.id.passQrDetect);
        qrHelper = new QrHelper();
        handler = new Handler()
        {

            @Override
            public void handleMessage(Message msg)
            {
                String text = qrHelper.readBarcode(qr_detect_activity.super.getApplicationContext(), (Bitmap)msg.obj);
                if (text != null)
                {
                    Crypto crypto = Crypto.parseBase64Encrypted(text);
                    crypto.password = edit.getText().toString();
                    crypto.decrypt();
                    textView.setText(crypto.genStringFromDecrypted());
                }
            }

        };

        navigation.getMenu().getItem(1).setChecked(true);
        if(!camera.isOpen() && camera.couldBeOpened)
        {
            camera.openCamera();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(camera.isOpen())
        {
            camera.closeCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results)
    {
        if(requestCode == CAMERA_PERMISSION_ID)
        {
            if(results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED)
            {
                camera = new CameraHelper(this, (TextureView) findViewById(R.id.camera_preview));
            }
        }
    }
}
