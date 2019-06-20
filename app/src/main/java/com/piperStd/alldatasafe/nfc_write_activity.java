package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.Core.Text;
import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;
import com.piperStd.alldatasafe.utils.Detectors.NfcHelper;

import java.nio.charset.StandardCharsets;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;
import static com.piperStd.alldatasafe.utils.Others.tools.toBytes;

public class nfc_write_activity extends AppCompatActivity {

    NfcAdapter adapter;
    ViewFlipper flipper = null;
    TextView nfcState;
    PendingIntent pending;
    IntentFilter[] filters;
    String[][] techList = null;

    byte service;
    String login = null;
    String password = null;
    String encrypt_pass = null;

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
        login = intent.getStringExtra("LOGIN");
        password = intent.getStringExtra("PASSWORD");
        service = intent.getByteExtra("SERVICE", AuthServices.VK);
        encrypt_pass = intent.getStringExtra("ENCRYPT_PASS");
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
        if(login != null && password != null && encrypt_pass != null)
        {
            try
            {
                AuthNode node = new AuthNode(service, login, password);
                String base64 = node.getEncryptedString(encrypt_pass);
                if (nfcHelper.writeTag(NfcHelper.TYPE_DATA, base64.getBytes(StandardCharsets.UTF_8))) {
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
