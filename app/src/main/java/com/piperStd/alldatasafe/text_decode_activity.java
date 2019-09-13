package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.UI.MainNavigationListener;
import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;
import com.piperStd.alldatasafe.utils.Detectors.NFC.NfcHelper;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class text_decode_activity  extends AppCompatActivity implements NavigationView.OnClickListener {

    ViewFlipper flipper = null;
    MainNavigationListener navListener = null;
    NavigationView navigation = null;
    DrawerLayout drawerLayout;

    EditText chiphertext;
    EditText encryption_pass;
    Button decrypt_btn;

    TextView login = null;
    TextView pass = null;
    AppCompatImageView serviceImg = null;

    byte[] key;

    NfcAdapter adapter = null;
    PendingIntent pending = null;
    IntentFilter[] filters = null;
    String[][] techList;


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
        adapter = NfcAdapter.getDefaultAdapter(this);
        pending = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        filters = new IntentFilter[]{NfcHelper.createNdefFilter(NfcHelper.TYPE_KEY), NfcHelper.createTechFilter()};
        techList = new String[][]{NfcHelper.knownTech};
    }

    @Override
    protected void onStart() {
        super.onStart();
        flipper.setDisplayedChild(7);
        navigation.getMenu().getItem(1).setChecked(true);
        key = new byte[256];
        chiphertext = findViewById(R.id.chiphertext_field);
        encryption_pass = findViewById(R.id.text_encryption_pass);
        login = findViewById(R.id.text_login_show);
        pass = findViewById(R.id.text_password_show);
        serviceImg = findViewById(R.id.text_service_icon);
        decrypt_btn = findViewById(R.id.text_decrypt_button);
        decrypt_btn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF)) {
            adapter.enableForegroundDispatch(this, pending, filters, techList);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
        {
            NfcHelper helper = new NfcHelper(intent.getExtras());
            this.key = Base64.decode(helper.readTag(), Base64.DEFAULT);
            encryption_pass.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (encryption_pass.isEnabled()) {
            if (encryption_pass.getText().toString() != "")
            {
                key = Crypto.getKDF(encryption_pass.getText().toString());
            }
            else {
                showException(this, "Empty encryption password!");
                return;
            }
        }
        AuthNode node = AuthNode.DecryptAndParse(chiphertext.getText().toString(), key);
        login.setText(node.login);
        pass.setText(node.password);
        switch (node.service) {
            case AuthServices.VK:
                serviceImg.setImageResource(R.drawable.ic_vk);
                break;
            case AuthServices.GITHUB:
                serviceImg.setImageResource(R.drawable.ic_github);
                break;
            case AuthServices.INSTAGRAM:
                serviceImg.setImageResource(R.drawable.ic_instagram);
                break;
            case AuthServices.STEAM:
                serviceImg.setImageResource(R.drawable.ic_steam);
                break;
        }

    }
}
