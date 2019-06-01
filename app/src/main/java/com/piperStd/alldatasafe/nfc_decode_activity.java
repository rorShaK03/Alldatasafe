package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.utils.Crypto;
import com.piperStd.alldatasafe.utils.NFC;

import java.nio.charset.StandardCharsets;

import static com.piperStd.alldatasafe.utils.tools.showException;
import static com.piperStd.alldatasafe.utils.tools.toBytes;

public class nfc_decode_activity extends AppCompatActivity {

    NfcAdapter adapter;

    DrawerLayout drawerLayout = null;
    NavigationView navigation;
    EditText passField = null;
    TextView decryptedField = null;
    ViewFlipper flipper = null;
    PendingIntent pending;
    IntentFilter[] filters;
    String[][] techList = null;
    ActivityLauncher launcher = null;
    MainNavigationListener navListener = null;

    String pass = null;
    String text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launcher = new ActivityLauncher(this);
        setContentView(R.layout.navigate_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        passField = findViewById(R.id.passDecodeField);
        decryptedField = findViewById(R.id.decryptedField);
        drawerLayout = findViewById(R.id.drawer_layout);
        navListener = new MainNavigationListener(this, drawerLayout);
        navigation = findViewById(R.id.nav_view);
        flipper = findViewById(R.id.viewFlipper);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigation.setNavigationItemSelectedListener(navListener);
        adapter = NfcAdapter.getDefaultAdapter(this);
        pending = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        filters = new IntentFilter[]{NFC.createNdefFilter(NFC.TYPE_DATA), NFC.createTechFilter()};
        techList = new String[][]{NFC.knownTech};
    }

    @Override
    protected void onStart() {
        super.onStart();
        flipper.setDisplayedChild(3);
        navigation.getMenu().getItem(1).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this, pending, filters, techList);

    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onStop() {
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
    public void onNewIntent(Intent intent) {
        NFC nfc = new NFC(intent.getExtras());
        Crypto crypto = Crypto.parseBase64Encrypted(new String(nfc.readTag()));
        crypto.password = passField.getText().toString();
        crypto.decrypt();
        decryptedField.setText("Расшифрованные данные: " + crypto.genStringFromDecrypted());
    }

}

