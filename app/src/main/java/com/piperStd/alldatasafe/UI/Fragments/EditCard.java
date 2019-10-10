package com.piperStd.alldatasafe.UI.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;

import com.piperStd.alldatasafe.Core.AuthNode;
import com.piperStd.alldatasafe.R;
import com.piperStd.alldatasafe.utils.Others.tools;

public class EditCard extends Fragment
{
    CardView card = null;
    AppCompatImageView service_icon = null;
    AuthNode node = null;
    TextView login = null;
    TextView pass = null;
    View view = null;
    boolean visible = false;

    public void setArguments(AuthNode node)
    {
        this.node = node;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.edit_card_layout, null);
        login = view.findViewById(R.id.edit_card_login_show);
        pass = view.findViewById(R.id.edit_card_password_show);
        service_icon = view.findViewById(R.id.edit_card_service_icon);
        card = view.findViewById(R.id.edit_card);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                visible = !visible;
                if(visible)
                    pass.setTransformationMethod(null);
                else
                    pass.setTransformationMethod(new PasswordTransformationMethod());
            }
        });
        login.setText(node.login);
        pass.setText(node.password);
        pass.setTransformationMethod(new PasswordTransformationMethod());
        tools.processService(node.service, service_icon);
        return view;
    }
}
