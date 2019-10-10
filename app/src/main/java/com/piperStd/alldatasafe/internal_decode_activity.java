package com.piperStd.alldatasafe;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.UI.Fragments.CryptCard;
import com.piperStd.alldatasafe.UI.Fragments.DecryptCard;
import com.piperStd.alldatasafe.UI.MainNavigationListener;
import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;
import com.piperStd.alldatasafe.utils.Detectors.NFC.NfcHelper;
import com.piperStd.alldatasafe.utils.Files.FileHelper;

import java.io.BufferedReader;
import java.io.FileReader;

import static com.piperStd.alldatasafe.utils.Others.tools.showException;

public class internal_decode_activity extends AppCompatActivity implements View.OnClickListener {

    ViewFlipper flipper = null;
    MainNavigationListener navListener = null;
    NavigationView navigation = null;
    DrawerLayout drawerLayout;

    ScrollView scroll = null;
    LinearLayout frags = null;
    Button decrypt_btn;
    EditText encryption_pass;
    CheckBox useNfc;
    CheckBox edit_mode_chx;
    FrameLayout[] frames = new FrameLayout[10];


    byte[] key = null;
    boolean nfc_used = false;
    boolean edit_mode = false;
    byte card_i = 0;

    DecryptCard[] cards = new DecryptCard[10];
    CryptCard[] edit_cards = new CryptCard[10];
    AuthNode[] nodes = new AuthNode[10];
    int node_c = 0;

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
        decrypt_btn = findViewById(R.id.internal_decrypt_button);
        frags = findViewById(R.id.internal_frags);
        scroll = findViewById(R.id.internal_scroll_view);
        edit_mode_chx = findViewById(R.id.edit_chx);
        nodesInit();
        decrypt_btn.setOnClickListener(this);
        encryption_pass = findViewById(R.id.internal_encryption_pass);
        useNfc = findViewById(R.id.internal_use_nfc);
        useNfc.setOnClickListener(this);
        edit_mode_chx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_mode_chx.isChecked()) edit_mode = true;
                else edit_mode = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF)) {
            adapter.enableForegroundDispatch(this, pending, filters, techList);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(edit_mode)
        {

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
                    showException(this, "Введите пароль или приложите NFC-ключ!");
                    return;
                }
            }
            if(key != null)
            {
                ReadTask task = new ReadTask(this);
                task.execute();
            }
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
        public AuthNode[] nodes = null;
        Context context = null;

        public ReadTask(Context context)
        {
            this.context = context;
        }
        @Override
        public Void doInBackground(Void[] params)
        {
            try
            {
                FileHelper file_helper = new FileHelper(context);
                String encrypted = file_helper.read_from_encrypted();
                AuthNode[] nodes = AuthNode.DecryptAndParseArray(encrypted, key);
                this.nodes = nodes;
            }
            catch (Exception e)
            {
                showException(this, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            if(nodes != null)
            {
                addNodes(nodes);
            }
        }
    }

    private void nodesInit()
    {
        for(int i = 0; i < 10; i++)
        {
            frames[i] = new FrameLayout(this);
            frames[i].setId(i + 1);
            frags.addView(frames[i]);
        }
    }

    private void addNodes(AuthNode[] nodes)
    {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        for(int i = 0; i < card_i; i++)
        {
            trans.remove(cards[i]);
            cards[i] = null;
        }
        card_i = 0;
        trans.commit();
        for(int i = 0; i < nodes.length; i++)
            addNode(nodes[i]);
    }

    private void addNode(AuthNode node)
    {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        cards[card_i] = new DecryptCard();
        cards[card_i].setArguments(node);
        trans.add(frames[card_i].getId(), cards[card_i]);
        trans.commit();
        card_i++;
    }

    /*
    private void addEditNode(AuthNode node)
    {
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        edit_cards[card_i] = new CryptCard();
        edit_cards[card_i].setArguments(node);
        trans.add(frames[card_i].getId(), edit_cards[card_i]);
        trans.commit();
        card_i++;
    }
    */
}
