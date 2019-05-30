package com.piperStd.cryptosaver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ViewFlipper;

public class NFC_write_activity extends AppCompatActivity {

    ViewFlipper flipper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigate_screen);
        flipper = findViewById(R.id.viewFlipper);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        flipper.setDisplayedChild(2);

    }

}
