package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.R;
import com.piperStd.alldatasafe.utils.camera.CameraHelper;

public class qr_detect_activity extends AppCompatActivity {

    final int CAMERA_PERMISSION_ID = 0;
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
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        flipper.setDisplayedChild(4);
        navigation.getMenu().getItem(1).setChecked(true);
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
