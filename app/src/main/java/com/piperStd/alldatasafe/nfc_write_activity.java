package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.UI.ActivityLauncher;
import com.piperStd.alldatasafe.UI.MainNavigationListener;
import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;
import com.piperStd.alldatasafe.utils.Detectors.NFC.NfcHelper;

import java.nio.charset.StandardCharsets;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class nfc_write_activity extends AppCompatActivity {

    NfcAdapter adapter;
    ViewFlipper flipper = null;
    NavigationView navigation;
    TextView nfcState;
    PendingIntent pending;
    IntentFilter[] filters;
    String[][] techList = null;

    String string_data = null;
    byte data_type = NfcHelper.TYPE_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        navigation = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        navigation.setNavigationItemSelectedListener(new MainNavigationListener(this, drawerLayout));
        toggle.syncState();
        flipper = findViewById(R.id.viewFlipper);
        adapter = NfcAdapter.getDefaultAdapter(this);
        pending = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        filters = new IntentFilter[]{NfcHelper.createNdefFilter(NfcHelper.TYPE_DATA), NfcHelper.createTechFilter()};
        techList = new String[][]{NfcHelper.knownTech};
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        flipper.setDisplayedChild(2);
        nfcState = findViewById(R.id.textNfcWrite);
        Intent intent = getIntent();
        switch(intent.getAction()) {
            case "authNode":
                String encrypted = intent.getStringExtra("ENCRYPTED");
                if(encrypted != null)
                {
                    string_data = encrypted;
                    data_type = NfcHelper.TYPE_DATA;
                }
                else
                    showException(this, "Could`t parse credentials");
                break;
            case "keygen":
                navigation.getMenu().getItem(2).setChecked(true);
                string_data = Base64.encodeToString(Crypto.keygen256(), Base64.DEFAULT);
                data_type = NfcHelper.TYPE_KEY;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        adapter.enableForegroundDispatch(this, pending, filters, techList);

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        NfcHelper nfcHelper = new NfcHelper(intent.getExtras());
        if(string_data != null && data_type != NfcHelper.TYPE_UNKNOWN)
        {
            try
            {
                if (nfcHelper.writeTag(data_type, string_data.getBytes(StandardCharsets.UTF_8))) {
                    nfcState.setText("Запись успешно произведена");
                    nfcState.setTextColor(Color.GREEN);
                    Thread.sleep(1000);
                    finish();
                } else {
                    nfcState.setText("Ошибка! Повторите попытку");
                    nfcState.setTextColor(Color.RED);
                }
            }
            catch(Exception e)
            {
                showException(this, e.getMessage());
            }
        }
    }

}
