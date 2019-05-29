package com.piperStd.cryptosaver;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import static com.piperStd.cryptosaver.utils.tools.*;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_crypt_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigation = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        AppCompatButton nextBtn = findViewById(R.id.button);
        nextBtn.setOnClickListener(this);
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
            intent.putExtra("text", text);
            intent.putExtra("pass", password);
            startActivity(intent);
        }
        catch(Exception e)
        {
            showException(this, e.getMessage());
        }
    }

}

