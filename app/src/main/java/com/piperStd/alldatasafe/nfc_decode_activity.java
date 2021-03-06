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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.utils.Detectors.NFC.NfcHelper;
import com.piperStd.alldatasafe.UI.ActivityLauncher;
import com.piperStd.alldatasafe.UI.MainNavigationListener;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

    /*
public class nfc_decode_activity extends AppCompatActivity {

    NfcAdapter adapter;

    DrawerLayout drawerLayout = null;
    NavigationView navigation;
    EditText passField = null;
    TextView login_field = null;
    TextView pass_field = null;
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
        passField = findViewById(R.id.passNfcDecode);
        login_field = findViewById(R.id.nfc_login_show);
        pass_field = findViewById(R.id.nfc_password_show);
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
        filters = new IntentFilter[]{NfcHelper.createNdefFilter(NfcHelper.TYPE_DATA), NfcHelper.createTechFilter()};
        techList = new String[][]{NfcHelper.knownTech};
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
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
        {
            NfcHelper nfcHelper = new NfcHelper(intent.getExtras());
            AuthNode node = AuthNode.DecryptAndParse(new String(nfcHelper.readTag()), passField.getText().toString());
            if (node != null) {
                login_field.setText(node.login);
                pass_field.setText(node.password);
            }
        }
    }


}
    */

