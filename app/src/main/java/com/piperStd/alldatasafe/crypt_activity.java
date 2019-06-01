package com.piperStd.alldatasafe;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;

import static com.piperStd.alldatasafe.utils.tools.*;

import com.google.android.material.navigation.NavigationView;


public class crypt_activity extends AppCompatActivity implements View.OnClickListener{

    ViewFlipper flipper = null;
    NavigationView navigation = null;
    AppCompatButton nextBtn;
    DrawerLayout drawerLayout;
    ActivityLauncher launcher = null;
    MainNavigationListener navListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        flipper.setDisplayedChild(0);
        navigation.getMenu().getItem(0).setChecked(true);
        nextBtn = findViewById(R.id.button);
        nextBtn.setOnClickListener(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public void onClick(View view)
    {

        RadioGroup place = findViewById(R.id.placeGroup);
        int placeId = place.getCheckedRadioButtonId();
        EditText editText = findViewById(R.id.editText);
        String text = editText.getText().toString();
        EditText editPass = findViewById(R.id.editPass);
        String pass = editPass.getText().toString();
        if(text.length() != 0 && pass.length() != 0) {
            switch (placeId) {
                case R.id.radioNFC:
                    launcher.launchNFCActivity(text, pass);
                    break;
                case R.id.radioQR:
                    launcher.launchQRCodeActivity(text, pass);
                    break;
                case R.id.radioServer:
                    break;
                default:
                    showException(this, "Choose place for saving");
            }
        }
        else if(text.length() == 0)
        {
            showException(this, "Text is empty");
        }
        else if(pass.length() == 0)
        {
            showException(this, "Pass is empty");
        }

    }





}

