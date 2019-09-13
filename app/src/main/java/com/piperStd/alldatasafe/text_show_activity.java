package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.UI.MainNavigationListener;

public class text_show_activity extends AppCompatActivity {

    ViewFlipper flipper = null;
    MainNavigationListener navListener = null;
    NavigationView navigation = null;
    DrawerLayout drawerLayout;

    TextView text_field;

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
        flipper.setDisplayedChild(6);
        navigation.getMenu().getItem(0).setChecked(true);
        Intent intent = getIntent();
        String login = intent.getStringExtra("LOGIN");
        String password = intent.getStringExtra("PASSWORD");
        byte service = intent.getByteExtra("SERVICE", AuthServices.UNKNOWN);
        byte[] key = intent.getByteArrayExtra("KEY");
        AuthNode node = new AuthNode(service, login, password);
        text_field = findViewById(R.id.text_field);
        text_field.setText(node.getEncryptedString(key));
    }
}
