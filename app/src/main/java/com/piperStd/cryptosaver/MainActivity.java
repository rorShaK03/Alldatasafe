package com.piperStd.cryptosaver;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.view.menu.MenuView;
import android.view.MenuItem;
import android.view.View;
import com.piperStd.cryptosaver.debug.qr_show_activity;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_crypt_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigation = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        AppCompatButton nextBtn = findViewById(R.id.button);
        nextBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        Intent intent = new Intent(this, qr_show_activity.class);
        startActivity(intent);
    }

}
