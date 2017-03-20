package com.jets.mashaweer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.jets.constants.SharedPreferenceInfo;

/**
 * Created by michael on 3/19/17.
 */

public class NavBar extends Fragment{
    private ImageButton homeBtn, historyBtn, addBtn, signoutBtn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nav_bar, container, false);

        homeBtn = (ImageButton) rootView.findViewById(R.id.home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));

            }
        });
        historyBtn = (ImageButton) rootView.findViewById(R.id.history);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HistoryActivity.class));

            }
        });
        addBtn = (ImageButton) rootView.findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TripAddActivity.class));

            }
        });
        signoutBtn = (ImageButton) rootView.findViewById(R.id.signout);
        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                SharedPreferenceInfo.addUserDataToSharedPreference(getActivity(),  "null");
            }
        });
        return rootView;
    }
}
