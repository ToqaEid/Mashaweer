package com.jets.mashaweer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jets.adapters.TabsAdapter;
import com.jets.classes.ListFormat;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.constants.DBConstants;
import com.jets.constants.SharedPreferenceInfo;
import com.jets.fragments.NavBarFragment;
import com.jets.fragments.PastTripsFragment;
import com.jets.fragments.UpcomingTripsFragment;
import com.jets.interfaces.Communicator;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ActionBar.TabListener , Communicator {

    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private ActionBar actionBar;
    public static String userID ;
    public UpcomingTripsFragment upcomingTripsFragment;
    public PastTripsFragment pastTripsFragment;

    private ArrayList<Trip> pastTrips;
    private ArrayList<Trip> upcomingTrips;
    private ArrayList<Trip> roundTrips;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Mashaweer");

        setContentView(R.layout.activity_main_home);

        NavBarFragment fragment = (NavBarFragment) getSupportFragmentManager().findFragmentById(R.id.navbar);
        fragment.setBtnColor("home");

        viewPager = (ViewPager) findViewById(R.id.pager);
        //getting userID from intent
        userID = getIntent().getStringExtra("userID");

        actionBar = getSupportActionBar();
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        upcomingTrips = new ArrayList<>();
        roundTrips = new ArrayList<>();
        pastTrips = new ArrayList<>();

        pastTripsFragment = tabsAdapter.getPastTripsFragment();
        upcomingTripsFragment = tabsAdapter.getUpcomingTripsFragment();

        viewPager.setAdapter(tabsAdapter);

        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        ///////// add your tabs
        for(int i=0; i<TabsAdapter.tabNames.length; i++)
        {
            ActionBar.Tab tab = actionBar.newTab().setText( TabsAdapter.tabNames[i].toString() ).setTabListener(this);
            actionBar.addTab(tab);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();


        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        // reading and updating data from database
        userID = SharedPreferenceInfo.getUserId(this);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference db = database.getReference("users/" + userID);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                upcomingTrips.clear();
                roundTrips.clear();
                pastTrips.clear();
                //TODO: clear all the other lists as well

                Iterable<DataSnapshot> trips = dataSnapshot.child("trips").getChildren();

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
                boolean alarmflag = sharedPreferences.getBoolean(SharedPreferenceInfo.ALARMS_SET, false);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                while (trips.iterator().hasNext()) {

                    DataSnapshot returnedData = trips.iterator().next();
                    Trip trip = returnedData.getValue(Trip.class);
                    trip.setTripId(returnedData.getKey());

                    //changing notes to empty array if null
                    if (trip.getTripCheckedNotes() == null) {
                        trip.setTripCheckedNotes(new ArrayList<String>());
                    }

                    if (trip.getTripUncheckedNotes() == null) {
                        trip.setTripUncheckedNotes(new ArrayList<String>());
                    }


                    switch (trip.getTripStatus()) {

                        case DBConstants.STATUS_UPCOMING:
                            upcomingTrips.add(trip);
                            Log.i("3lama", "Alaram Flag " + alarmflag);
                            if (alarmflag) {
                                if (trip.getTripDateTime() > System.currentTimeMillis())
                                    TripServices.setAlarm(HomeActivity.this, trip);

                            }
                            break;

                        case DBConstants.STATUS_PENDING:
                            roundTrips.add(trip);
                            Log.i("3lama + round trips", " " + trip.toString());

                            break;

                        default:
                            pastTrips.add(trip);
                            break;
                    }
                }
                Log.i("home", "Home "+pastTrips.size());

                pastTripsFragment.refreshData(pastTrips);
                upcomingTripsFragment.refreshData(upcomingTrips, roundTrips);
                editor.putBoolean(SharedPreferenceInfo.ALARMS_SET, false);
                editor.commit();
                alarmflag = sharedPreferences.getBoolean(SharedPreferenceInfo.ALARMS_SET, false);

                Log.i("3lama", "changing flag to false Flag " + alarmflag );
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

    }

    //////////////////////// tabs ya basha

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void sendMsg(Trip trip) {

        Toast.makeText(this, "Going To TripDetailsActivity", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(),TripDetailsActivity.class);
        intent.putExtra("selectedTrip", trip);
        startActivity(intent);

    }
}
