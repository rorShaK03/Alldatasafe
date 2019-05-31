package com.piperStd.alldatasafe;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;

import static com.piperStd.alldatasafe.utils.tools.*;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.utils.NFC;


public class crypt_activity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    ViewFlipper flipper = null;
    AppCompatButton nextBtn;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        flipper = findViewById(R.id.viewFlipper);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigation = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigation.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        flipper.setDisplayedChild(0);
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
                    launchNFCActivity(text, pass);
                    break;
                case R.id.radioQR:
                    launchQRCodeActivity(text, pass);
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

    private void launchQRCodeActivity(String text, String password)
    {
        try {
            Intent intent = new Intent(this, qr_show_activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("com.piperstd.alldatasafe.EXTRA_TEXT", text);
            intent.putExtra("com.piperstd.alldatasafe.EXTRA_PASS", password);
            startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    private void launchNFCActivity(String text, String password)
    {
        try {
            Intent intent = new Intent(this, nfc_write_activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("com.piperstd.alldatasafe.EXTRA_TEXT", text);
            intent.putExtra("com.piperstd.alldatasafe.EXTRA_PASS", password);
            startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    private void launchNfcDecodeActivity()
    {
        try {
            Intent intent = new Intent(this, nfc_decode_activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    private void launchCryptActivity()
    {
        try {
            Intent intent = new Intent(this, crypt_activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.nav_encrypt:
                this.launchCryptActivity();
                break;
            case R.id.nav_decrypt:
                this.launchNfcDecodeActivity();
        }
        return true;
    }

}

