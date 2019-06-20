package com.piperStd.alldatasafe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;

import static com.piperStd.alldatasafe.utils.Others.tools.*;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.UI.ActivityLauncher;
import com.piperStd.alldatasafe.UI.MainNavigationListener;


public class crypt_activity extends AppCompatActivity implements View.OnClickListener{

    byte service = AuthServices.GITHUB;
    ViewFlipper flipper = null;
    Context context = null;
    AppCompatImageView service_icon = null;
    NavigationView navigation = null;
    AppCompatButton nextBtn;
    DrawerLayout drawerLayout;
    ActivityLauncher launcher = null;
    MainNavigationListener navListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
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
        //launcher.launchQrDetectActivity();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        flipper.setDisplayedChild(0);
        navigation.getMenu().getItem(0).setChecked(true);
        nextBtn = findViewById(R.id.button);
        service_icon = findViewById(R.id.service_icon);
        nextBtn.setOnClickListener(this);
        service_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId())
                        {
                            case R.id.vk_item:
                                service_icon.setImageResource(R.drawable.ic_vk);
                                service = AuthServices.VK;
                                return true;
                            case R.id.github_item:
                                service_icon.setImageResource(R.drawable.ic_github);
                                service = AuthServices.GITHUB;
                                return true;
                            default:
                                return false;

                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.services_menu, popup.getMenu());
                @SuppressLint("apiRestricted")
                MenuPopupHelper helper = new MenuPopupHelper(context, (MenuBuilder)popup.getMenu(), v);
                helper.setForceShowIcon(true);
                helper.show();
            }
        });
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
        EditText login_field = findViewById(R.id.login_field);
        EditText password_field = findViewById(R.id.password_field);
        String login = login_field.getText().toString();
        String password = password_field.getText().toString();
        EditText editPass = findViewById(R.id.editPass);
        String encryption_pass = editPass.getText().toString();
        if(password.length() != 0 && login.length() != 0 && encryption_pass.length() != 0) {
            switch (placeId) {
                case R.id.radioQR:
                    launcher.launchQRCodeActivity(service, login, password, encryption_pass);
                    break;
                case R.id.radioText:
                    break;
                default:
                    showException(this, "Choose place for saving");
            }
        }
        else if(login.length() == 0)
        {
            showException(this, "Login is empty");
        }
        else if(password.length() == 0)
        {
            showException(this, "Password is empty");
        }
        else if(encryption_pass.length() == 0)
        {
            showException(this, "Encryption password is empty");
        }

    }





}

