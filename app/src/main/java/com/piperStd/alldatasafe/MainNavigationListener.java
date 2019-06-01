package com.piperStd.alldatasafe;

import android.content.Context;
import android.view.Gravity;
import android.view.MenuItem;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainNavigationListener implements NavigationView.OnNavigationItemSelectedListener
{
    private Context context;
    private ActivityLauncher launcher = null;
    private DrawerLayout drawer;

    public MainNavigationListener(Context context, DrawerLayout drawer)
    {
        this.context = context;
        this.drawer = drawer;
        launcher = new ActivityLauncher(context);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.nav_encrypt:
                launcher.launchCryptActivity();
                break;
            case R.id.nav_decrypt:
                launcher.launchNfcDecodeActivity();
        }
        drawer.closeDrawer(Gravity.LEFT);
        return true;
    }
}
