package com.piperStd.alldatasafe;

import android.annotation.SuppressLint;
import androidx.appcompat.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
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

import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.widget.PopupMenu;
import android.app.Fragment;

import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.ViewFlipper;

import static com.piperStd.alldatasafe.utils.Others.tools.*;

import com.google.android.gms.auth.api.Auth;
import com.google.android.material.navigation.NavigationView;
import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.UI.ActivityLauncher;
import com.piperStd.alldatasafe.UI.Fragments.CryptCard;
import com.piperStd.alldatasafe.UI.GifView;
import com.piperStd.alldatasafe.UI.MainNavigationListener;
import com.piperStd.alldatasafe.utils.Cryptographics.Crypto;
import com.piperStd.alldatasafe.utils.Detectors.NFC.NfcHelper;

import java.io.InputStream;


public class crypt_activity extends AppCompatActivity implements View.OnClickListener{



    ViewFlipper flipper = null;
    public static Context context = null;
    NavigationView navigation = null;
    DrawerLayout drawerLayout;
    ActivityLauncher launcher = null;
    MainNavigationListener navListener = null;

    EditText editPass;
    CheckBox useNfc;
    Button nextBtn;
    Button add_btn;
    ScrollView scroll;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    LinearLayout frags;
    FrameLayout[] frames = new FrameLayout[10];

    CryptCard[] cards = new CryptCard[10];
    byte card_i = 0;

    boolean nfc_used = false;

    NfcAdapter adapter = null;
    PendingIntent pending = null;
    IntentFilter[] filters = null;
    String[][] techList;

    byte[] key = new byte[0];

    Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        launcher = new ActivityLauncher(this);
        setContentView(R.layout.navigate_screen);
        toolbar = findViewById(R.id.toolbar);
        flipper = findViewById(R.id.viewFlipper);
        drawerLayout = findViewById(R.id.drawer_layout);
        navListener = new MainNavigationListener(this, drawerLayout);
        navigation = findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigation.setNavigationItemSelectedListener(navListener);
        // Переменные для взаимодействия с NFC
        adapter = NfcAdapter.getDefaultAdapter(this);
        pending = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        filters = new IntentFilter[]{NfcHelper.createNdefFilter(NfcHelper.TYPE_KEY), NfcHelper.createTechFilter()};
        techList = new String[][]{NfcHelper.knownTech};
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onStart()
    {
        super.onStart();
        //Установка элементов интерфейса
        flipper.setDisplayedChild(0);
        navigation.getMenu().getItem(0).setChecked(true);
        getSupportActionBar().setTitle("");
        editPass = findViewById(R.id.editPass);
        nextBtn = findViewById(R.id.next_btn);
        add_btn = findViewById(R.id.add_btn);
        useNfc = findViewById(R.id.use_nfc);
        // Контейнер для карточек для ввода логина и пароля
        frags = findViewById(R.id.crypt_frags);
        scroll = findViewById(R.id.crypt_scroll);
        useNfc.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        // Если нет ни одной карточки создаем одну
        if(card_i == 0)
        {
            addCard();
            //cards[0].closable = false;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Если ест поддержка NFC, включаем обработчик
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF)) {
            adapter.enableForegroundDispatch(this, pending, filters, techList);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // Выключаем обработчик NFC
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION_NFCF)) {
            adapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // При выходе закрываем боковое меню
        drawerLayout.closeDrawers();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        // Если поднесен NFC-тег
        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
        {
            // Читаем ключ с тега
            NfcHelper helper = new NfcHelper(intent.getExtras());
            this.key = Base64.decode(helper.readTag(), Base64.DEFAULT);
            editPass.setText("");
            editPass.setEnabled(false);
            // Ставим галочку NFC-ключ
            useNfc.setChecked(true);
            nfc_used = true;
        }
    }

    //При нажатии кнопки "назад", закрываем меню
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
        // Если нажата кнопка "Продолжить"
        if(view.getId() == nextBtn.getId()) {
            boolean anyEmptyField = false;
            // Узнаем, куда пользователь хочет сохранить
            RadioGroup place = findViewById(R.id.placeGroup);
            int placeId = place.getCheckedRadioButtonId();
            // Преобразуем ввод в массив AuthNode
            AuthNode[] nodes = new AuthNode[card_i];
            for (int i = 0; i < card_i; i++)
            {
                nodes[i] = cards[i].getNode();
                //Если вернулась пустая карточка
                if(nodes[i] == null)
                    anyEmptyField = true;
            }
            // Если ни одной карточки
            if(card_i == 0)
                anyEmptyField = true;
            // Получаем ключ для шифрования
            byte[] key_bytes = null;
            // Если пользователь использует пароль для шифрования
            if (!nfc_used && !(editPass.getText().toString().length() == 0))
                key_bytes = Crypto.getKDF(editPass.getText().toString());
            //Если использовал NFC-ключ
            else if (nfc_used)
                key_bytes = this.key;
                if (!anyEmptyField && key_bytes != null)
                {
                    // Получаем строку с шифрованным массивом AuthNode
                    String encrypted = AuthNode.getEncryptedStringFromArray(nodes, key_bytes);
                    // И сохраняем
                    switch (placeId) {
                        case R.id.radioQR:
                            launcher.launchQRCodeActivity(encrypted);
                            break;
                        case R.id.radioInternal:
                            launcher.launchInternalShowActivity(encrypted);
                            break;
                        default:
                            showException(this, "Выберите место для хранения данных!");
                    }
                } else if (anyEmptyField) {
                    showException(this, "Хотя бы одна учетная запись заполнена не до конца!");
                } else if (key_bytes == null) {
                    showException(this, "Введите пароль или воспользуйтесь NFC-ключом!");
                }
            }

        else if(view.getId() == useNfc.getId())
        {
            // Если нажата галочка "NFC-ключ"
            if(useNfc.isChecked() == true)
            {
                editPass.setText("");
                editPass.setEnabled(false);
            }
            else
            {
                nfc_used = false;
                editPass.setEnabled(true);
            }
        }
        // Если нажата кнопка "Добавить"
        else if(view.getId() == add_btn.getId())
        {
            if(card_i < 10)
                addCard();
            else
                showException(this, "Добавлено максимальное число записей!");
        }

    }

    // Функция для добавления новой карточки
    private void addCard()
    {
        try
        {
            // Создаем новый контейнер для фрагмента
            final FrameLayout current = new FrameLayout(this);
            // Устанавливаем ему id
            current.setId(card_i + 1);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            current.setLayoutParams(params);
            // Скидываем в массив для таких контейнеров
            frames[card_i] = current;
            // Добавляем в основной контейнер
            frags.addView(frames[card_i]);
            //Создаем новую карточку(фрагмент)
            cards[card_i] = new CryptCard();
            cards[card_i].setArguments(this, card_i);
            //Выводим карточку в интерфейс
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.add(frames[card_i].getId(), cards[card_i]);
            trans.commit();
            // Увеличиваем счетчик
            card_i++;
            // Прокручивание контейнера в самый низ
            scroll.postDelayed(new Runnable() {
                                   @Override
                                   public void run() {
                                       scroll.fullScroll(View.FOCUS_DOWN);
                                   }
                               }
                    , 100);
            /*
            if (card_i != 1) {
                cards[0].closable = true;
                cards[0].close_btn.setVisibility(View.VISIBLE);
            }
            */
        }
        catch(Exception e)
        {

        }
    }

    // Функция для удаления карточки
    public void deleteCard(int number)
    {
        //Костыль, фикс гонки добавление/удаление
        try
        {
            // 
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            trans.remove(cards[number]);
            trans.commit();
            frags.removeView(frames[number]);
            //frags.removeViewAt(number);
            card_i--;
            for (int i = number; i < card_i; i++) {
                cards[i] = cards[i + 1];
                cards[i].i--;
            }
            cards[card_i] = null;
            /*
            if(card_i == 1)
            {
                cards[0].closable = false;
                cards[0].close_btn.setVisibility(View.INVISIBLE);
            }
            */
        }
        catch(Exception e)
        {

        }
    }



}

