package com.jets.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.jets.mashaweer.HistoryActivity;
import com.jets.mashaweer.HomeActivity;
import com.jets.mashaweer.LoginActivity;
import com.jets.mashaweer.R;
import com.jets.mashaweer.TripAddActivity;
import com.jets.constants.SharedPreferenceInfo;

/**
 * Created by michael on 3/19/17.
 */

public class NavBarFragment extends Fragment{
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

                int [] networks = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};

                if (isNetworkAvailable( getContext() ,networks))
                {
                    Toast.makeText(getContext(), "Good Internet Connection", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), TripAddActivity.class));
                }
                else{

                    Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        signoutBtn = (ImageButton) rootView.findViewById(R.id.signout);
        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();


                SharedPreferenceInfo.signOut(getActivity().getApplicationContext());

            }
        });
        return rootView;
    }
    public void setBtnColor(String btn){
        switch (btn){
            case "home":
                homeBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.com_facebook_messenger_blue));
                break;
            case "map":
                historyBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.com_facebook_messenger_blue));
                break;

        }
    }


    public static boolean isNetworkAvailable(Context context, int[] networkTypes) {
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo netInfo = cm.getNetworkInfo(networkType);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
