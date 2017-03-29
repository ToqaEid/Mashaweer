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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jets.classes.ListFormat;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.constants.DBConstants;
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
    private ImageView homeBtn, historyBtn, addBtn, signoutBtn;
    private TextView homeText, historyText;
    private LinearLayout home, history, add, logout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nav_bar, container, false);
        home = (LinearLayout) rootView.findViewById(R.id.homeLayout);
        history = (LinearLayout) rootView.findViewById(R.id.historyLayout);
        add = (LinearLayout) rootView.findViewById(R.id.addLayout);
        logout = (LinearLayout) rootView.findViewById(R.id.logoutLayout);

        homeText = (TextView) rootView.findViewById(R.id.texthome);
        historyText = (TextView) rootView.findViewById(R.id.texthistory);

        homeBtn = (ImageView) rootView.findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(getActivity() instanceof HomeActivity)) {

                    startActivity(new Intent(getActivity(), HomeActivity.class));
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

            }
        });
        historyBtn = (ImageView) rootView.findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(getActivity() instanceof HistoryActivity)) {
                    startActivity(new Intent(getActivity(), HistoryActivity.class));
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                }

            }
        });
        addBtn = (ImageView) rootView.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int [] networks = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};

                if (isNetworkAvailable( getContext() ,networks) && !(getActivity() instanceof TripAddActivity))
                {
                    Toast.makeText(getContext(), "Good Internet Connection", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), TripAddActivity.class));
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else{

                    Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }
//                if(!(getActivity() instanceof TripAddActivity)) {
//                    startActivity(new Intent(getActivity(), TripAddActivity.class));
//
//                }

            }
        });
        signoutBtn = (ImageView) rootView.findViewById(R.id.signout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();

                String userID = SharedPreferenceInfo.getUserId(getActivity());

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference db = database.getReference("users/" + userID);

                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                        Iterable<DataSnapshot> trips = dataSnapshot.child("trips").getChildren();

                        while (trips.iterator().hasNext()) {

                            DataSnapshot returnedData = trips.iterator().next();
                            Trip trip = returnedData.getValue(Trip.class);

                            if (trip.getTripStatus() == DBConstants.STATUS_UPCOMING){
                                TripServices.deleteAlarm(getContext(), trip);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                SharedPreferenceInfo.signOut(getActivity().getApplicationContext());

            }
        });
        return rootView;
    }
    public void setBtnColor(String btn){
        switch (btn){
            case "home":
                homeBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.com_facebook_messenger_blue));
                homeText.setTextColor(ContextCompat.getColor(getContext(),R.color.com_facebook_messenger_blue));

                break;
            case "map":
                historyBtn.setColorFilter(ContextCompat.getColor(getContext(),R.color.com_facebook_messenger_blue));
                historyText.setTextColor(ContextCompat.getColor(getContext(),R.color.com_facebook_messenger_blue));
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
