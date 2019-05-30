package com.piperStd.cryptosaver;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.cryptosaver.utils.Crypto;
import com.piperStd.cryptosaver.utils.QR;
import com.piperStd.cryptosaver.utils.tools;

import static com.piperStd.cryptosaver.utils.tools.showException;

public class qr_show_activity extends AppCompatActivity {

    ImageView qrImage;
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
        qrImage = findViewById(R.id.imageView);
        try
        {
            Bundle extras = getIntent().getExtras();
            String text = (String)extras.get("com.piperstd.cryptosaver.EXTRA_TEXT");
            String password = (String)extras.get("com.piperstd.cryptosaver.EXTRA_PASS");
            Crypto crypto = new Crypto(tools.toBytes(text), password);
            crypto.encrypt();
            qrImage.setImageBitmap(QR.genBarcode(crypto.genEncryptedDataArr()));
        }
        catch(Exception e)
        {
            showException(this, "Unable to start qr_show_activity: " + e.getMessage());
        }

    }


}
