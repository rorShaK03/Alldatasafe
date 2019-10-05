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

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;

import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.R;
import com.piperStd.alldatasafe.crypt_activity;
import com.piperStd.alldatasafe.utils.Others.tools;

public class CryptCard extends Fragment
{
    public int i;
    AppCompatImageView service_icon = null;
    public AppCompatImageView close_btn = null;
    public boolean closable = true;
    View view = null;
    crypt_activity context = null;
    byte service = AuthServices.VK;

    public void setArguments(crypt_activity context, int i)
    {
        this.context = context;
        this.i = i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.crypt_card_layout, null);
        if(container.getChildCount() != 0)
            container.removeAllViews();
        service_icon = view.findViewById(R.id.card_service_icon);
        service_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        service = tools.processServiceChoice(item.getItemId(), service_icon);
                        return (service != -1);
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
        close_btn = view.findViewById(R.id.img_close);
        if(!closable)
            close_btn.setVisibility(View.INVISIBLE);
        close_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                context.deleteCard(i);
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
        if(login.length() != 0 && password.length() != 0)
            return new AuthNode(service, login, password);
        else
            return null;
    }


}
