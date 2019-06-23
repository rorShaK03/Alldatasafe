package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.utils.Detectors.QrHelper;
import com.piperStd.alldatasafe.UI.ActivityLauncher;
import com.piperStd.alldatasafe.UI.MainNavigationListener;
import com.piperStd.alldatasafe.utils.camera.CameraHelper;

public class qr_detect_activity extends AppCompatActivity {

    final int CAMERA_PERMISSION_ID = 0;
    AppCompatImageView serviceImage = null;
    TextView login_field;
    TextView pass_field;
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

    @Override
    protected void onStart()
    {
        super.onStart();
        flipper.setDisplayedChild(4);
        navigation.getMenu().getItem(1).setChecked(true);
        login_field = findViewById(R.id.qr_login_show);
        pass_field = findViewById(R.id.qr_password_show);
        serviceImage = findViewById(R.id.qr_service_icon);
        edit = findViewById(R.id.passQrDetect);
        qrHelper = new QrHelper();
        handler = new Handler()
        {

            @Override
            public void handleMessage(Message msg)
            {
                new DecryptTask(edit.getText().toString()).execute((Bitmap)msg.obj);
            }

        };
        if(camera != null && !camera.isOpen() && camera.couldBeOpened)
            camera.openCamera();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(camera != null && camera.isOpen())
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
                camera.openCamera();
            }
        }
    }

    class DecryptTask extends AsyncTask<Bitmap, Void, AuthNode>
    {
        private String pass;

        public DecryptTask(String pass)
        {
            this.pass = pass;
        }

        @Override
        protected AuthNode doInBackground(Bitmap[] params)
        {
            String text = qrHelper.readBarcode(qr_detect_activity.super.getApplicationContext(), params[0]);
            if (text != null)
            {
                AuthNode node = AuthNode.DecryptAndParse(text, pass);
                return node;
            }
            return null;
        }

        @Override
        protected void onPostExecute(AuthNode res)
        {
            if(res != null)
            {
                login_field.setText(res.login);
                pass_field.setText(res.password);
                switch(res.service)
                {
                    case AuthServices.VK:
                        serviceImage.setImageResource(R.drawable.ic_vk);
                        break;
                    case AuthServices.GITHUB:
                        serviceImage.setImageResource(R.drawable.ic_github);
                        break;
                    case AuthServices.INSTAGRAM:
                        serviceImage.setImageResource(R.drawable.ic_instagram);
                        break;
                    case AuthServices.STEAM:
                        serviceImage.setImageResource(R.drawable.ic_steam);
                        break;
                }
            }
        }
    }
}
