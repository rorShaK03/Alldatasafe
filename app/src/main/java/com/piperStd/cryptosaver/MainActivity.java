package com.piperStd.cryptosaver;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    CardView nfcCard;
    CardView qrCard;
    CardView serverCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nfcCard = findViewById(R.id.card_nfc);
        qrCard = findViewById(R.id.card_qr);
        serverCard = findViewById(R.id.card_server);

        View.OnTouchListener cardTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motion) {
                if(motion.getAction() == MotionEvent.ACTION_DOWN)
                    ((CardView)view).setCardElevation(4);
                else if(motion.getAction() == MotionEvent.ACTION_UP)
                    ((CardView)view).setCardElevation(6);
                return true;
            }
        };

        nfcCard.setOnTouchListener(cardTouchListener);
        qrCard.setOnTouchListener(cardTouchListener);
        serverCard.setOnTouchListener(cardTouchListener);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
