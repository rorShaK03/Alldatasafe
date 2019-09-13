package com.piperStd.alldatasafe.UI.Fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;

import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.R;
import com.piperStd.alldatasafe.crypt_activity;

public class CryptCard extends Fragment
{
    AppCompatImageView service_icon = null;
    View view = null;
    Context context = crypt_activity.context;
    byte service = AuthServices.VK;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.crypt_card_layout, null);
        service_icon = view.findViewById(R.id.service_icon);
        service_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId())
                        {
                            case R.id.vk_item:
                                service_icon.setImageResource(R.drawable.ic_vk);
                                service = AuthServices.VK;
                                return true;
                            case R.id.github_item:
                                service_icon.setImageResource(R.drawable.ic_github);
                                service = AuthServices.GITHUB;
                                return true;
                            case R.id.instagram_item:
                                service_icon.setImageResource(R.drawable.ic_instagram);
                                service = AuthServices.INSTAGRAM;
                                return true;
                            case R.id.steam_item:
                                service_icon.setImageResource(R.drawable.ic_steam);
                                service = AuthServices.STEAM;
                                return true;
                            default:
                                return false;

                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.services_menu, popup.getMenu());
                @SuppressLint("RestrictedApi")
                MenuPopupHelper helper = new MenuPopupHelper(context, (MenuBuilder)popup.getMenu(), v);
                helper.setForceShowIcon(true);
                helper.show();
            }
        });
        return view;
    }

    public AuthNode getNode()
    {
        EditText login_field = view.findViewById(R.id.login_field);
        EditText password_field = view.findViewById(R.id.password_field);
        String login = login_field.getText().toString();
        String password = password_field.getText().toString();
        return new AuthNode(service, login, password);
    }
}
