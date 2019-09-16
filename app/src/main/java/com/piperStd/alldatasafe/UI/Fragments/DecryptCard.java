package com.piperStd.alldatasafe.UI.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.Core.AuthServices;
import com.piperStd.alldatasafe.R;

public class DecryptCard extends Fragment
{

    AppCompatImageView service_icon = null;
    AuthNode node = null;
    TextView login = null;
    TextView pass = null;
    View view = null;

    public void setArguments(AuthNode node)
    {
        this.node = node;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.decrypt_card_layout, null);
        login = view.findViewById(R.id.card_login_show);
        pass = view.findViewById(R.id.card_password_show);
        service_icon = view.findViewById(R.id.card_service_icon);
        login.setText(node.login);
        pass.setText(node.password);
        switch (node.service) {
            case AuthServices.VK:
                service_icon.setImageResource(R.drawable.ic_vk);
                break;
            case AuthServices.GITHUB:
                service_icon.setImageResource(R.drawable.ic_github);
                break;
            case AuthServices.INSTAGRAM:
                service_icon.setImageResource(R.drawable.ic_instagram);
                break;
            case AuthServices.STEAM:
                service_icon.setImageResource(R.drawable.ic_steam);
                break;
        }
        return view;
    }
}
