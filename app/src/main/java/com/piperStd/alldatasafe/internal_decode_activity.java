package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.UI.MainNavigationListener;
import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;
import com.piperStd.alldatasafe.utils.Detectors.NFC.NfcHelper;
import com.piperStd.alldatasafe.utils.Files.FileHelper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class internal_decode_activity extends AppCompatActivity implements View.OnClickListener {

    ViewFlipper flipper = null;
    MainNavigationListener navListener = null;
    NavigationView navigation = null;
    DrawerLayout drawerLayout;

    Button decrypt_btn;
    EditText encryption_pass;
    CheckBox useNfc;

    TextView login = null;
    TextView pass = null;
    AppCompatImageView serviceImg = null;

    byte[] key = null;
    boolean nfc_used = false;

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
    protected void onStart()
    {
        super.onStart();
        flipper.setDisplayedChild(9);
        navigation.getMenu().getItem(1).setChecked(true);
        login = findViewById(R.id.internal_login_show);
        pass = findViewById(R.id.internal_password_show);
        serviceImg = findViewById(R.id.internal_service_icon);
        decrypt_btn = findViewById(R.id.internal_decrypt_button);
        decrypt_btn.setOnClickListener(this);
        encryption_pass = findViewById(R.id.internal_encryption_pass);
        useNfc = findViewById(R.id.internal_use_nfc);
        useNfc.setOnClickListener(this);
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
            encryption_pass.setText("");
            encryption_pass.setEnabled(false);
            useNfc.setChecked(true);
            nfc_used = true;
        }
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == decrypt_btn.getId())
        {
            if (!nfc_used) {
                if (!encryption_pass.getText().toString().equals("")) {
                    key = Crypto.getKDF(encryption_pass.getText().toString());
                } else {
                    showException(this, "Empty encryption password!");
                    return;
                }
            }
            if(key != null)
                new ReadTask().execute();
        }
        else if(view.getId() == useNfc.getId())
        {
            if(useNfc.isChecked() == true)
            {
                encryption_pass.setText("");
                encryption_pass.setEnabled(false);
            }
            else
            {
                nfc_used = false;
                encryption_pass.setEnabled(true);
            }
        }

    }

    class ReadTask extends AsyncTask<Void, Void, Void>
    {
        String FILENAME = "encrypted";
        @Override
        public Void doInBackground(Void[] params)
        {
            try
            {
                StringBuilder encrypted = new StringBuilder();
                String line;
                BufferedReader br = new BufferedReader(new FileReader(getApplicationContext().getFilesDir() + "/" + FILENAME));
                while((line = br.readLine()) != null)
                {
                    encrypted.append(line + '\n');
                }
                br.close();
                AuthNode node = AuthNode.DecryptAndParse(encrypted.toString(), key);
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
            catch (Exception e)
            {
                showException(this, e.getMessage());
            }
            return null;
        }
    }
}
