package com.jets.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jets.adapters.round.RoundListAdapter;
import com.jets.classes.TripServices;
import com.jets.constants.Alert;
import com.jets.constants.DBConstants;
import com.jets.classes.ListFormat;
import com.jets.mashaweer.DB_Adapter;
import com.jets.mashaweer.LoginActivity;
import com.jets.mashaweer.R;
import com.jets.classes.Trip;
import com.jets.constants.SharedPreferenceInfo;
import com.jets.adapters.UpcomingCustomAdapter;
import com.jets.interfaces.Communicator;
import com.jets.mashaweer.TripAddActivity;
import com.jets.mashaweer.TripDetailsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingTripsFragment extends Fragment {

    private ListView upcoming_listView;
    private ListView round_listView;
    private Communicator communicator;
    private UpcomingCustomAdapter adapter;
    private RoundListAdapter roundListAdapter;
    private LinearLayout upcomingHeader;
    private TextView roundTitle;

    private DB_Adapter db_adapter;
    private ArrayList<Trip> upcomingTrips = new ArrayList<>();
    private ArrayList<Trip> roundTrips = new ArrayList<>();
    private String userID;

    boolean isEmpty;

    public UpcomingTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        if (! isEmpty)
        {
             rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);

            upcoming_listView = (ListView)  rootView.findViewById(R.id.upcoming_listView);

            round_listView = (ListView) rootView.findViewById(R.id.round_listView);

            upcomingHeader = (LinearLayout) rootView.findViewById(R.id.round_title);

            adapter = new UpcomingCustomAdapter(getContext(),upcomingTrips );
            roundListAdapter = new RoundListAdapter(getContext(), roundTrips);

            adapter.notifyDataSetChanged();
            roundListAdapter.notifyDataSetChanged();

            upcoming_listView.setAdapter(adapter);
            round_listView.setAdapter(roundListAdapter);


//            ListFormat.setListViewHeightBasedOnChildren(upcoming_listView);
//            ListFormat.setListViewHeightBasedOnChildren(round_listView);

            registerForContextMenu(upcoming_listView);


            Log.i("MyTag","Upcoming adapter is set");
            upcoming_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Trip selectedTrip = upcomingTrips.get(position);
                    Log.i("trip", "setOnItemClickListener: "+ upcomingTrips.get(position).toString());

                    communicator.sendMsg(selectedTrip);
                }
            });
            upcoming_listView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return false;
                }
            });
            round_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    communicator.sendMsg(roundTrips.get(position));
                }
            });

        }else{

             rootView = inflater.inflate(R.layout.empty_listview, container, false);

        }

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        communicator = (Communicator) getActivity();
    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//
//        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
//                R.style.AppTheme_Dark_Dialog);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Fetching Data...");
//        progressDialog.show();
//
//        // reading and updating data from database
//        userID = SharedPreferenceInfo.getUserId(getActivity());
//
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference db = database.getReference("users/" + userID);
//
//        db.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                upcomingTrips.clear();
//                roundTrips.clear();
//                //TODO: clear all the other lists as well
//
//                Iterable<DataSnapshot> trips = dataSnapshot.child("trips").getChildren();
//
//                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
//                boolean alarmflag = sharedPreferences.getBoolean(SharedPreferenceInfo.ALARMS_SET, false);
//
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//
//                while (trips.iterator().hasNext()) {
//
//                    DataSnapshot returnedData = trips.iterator().next();
//                    Trip trip = returnedData.getValue(Trip.class);
//                    trip.setTripId(returnedData.getKey());
//
//                    //changing notes to empty array if null
//                    if (trip.getTripCheckedNotes() == null){
//                        trip.setTripCheckedNotes(new ArrayList<String>());
//                    }
//
//                    if (trip.getTripUncheckedNotes() == null){
//                        trip.setTripUncheckedNotes(new ArrayList<String>());
//                    }
//
//
//                    switch (trip.getTripStatus()) {
//
//                        case DBConstants.STATUS_UPCOMING:
//                            upcomingTrips.add(trip);
//                            Log.i("3lama", "Alaram Flag " + alarmflag);
//                            if (alarmflag){
//                                if (trip.getTripDateTime() > System.currentTimeMillis())
//                                TripServices.setAlarm(getActivity(), trip);
//
//                            }
//                            break;
//
//                        case DBConstants.STATUS_PENDING:
//                            roundTrips.add(trip);
//                            Log.i("3lama + round trips", " " + trip.toString());
//
//                            break;
//
//                        default:
//                            //TODO: Replace the array list "upcoming trips" with the done/cancelled arraylist name
//                            //upcomingTrips.add(trip);
//                            break;
//                    }
//
//
//
//                    if (adapter != null) {
//                        adapter.notifyDataSetChanged();
//                        roundListAdapter.notifyDataSetChanged();
//                        ListFormat.setListViewHeightBasedOnChildren(upcoming_listView);
//                        ListFormat.setListViewHeightBasedOnChildren(round_listView);
//                    }
//
//                }
//                if(roundTrips.size() == 0){
//                    round_listView.setVisibility(View.GONE);
//                    upcomingHeader.setVisibility(View.GONE);
//
//                }else {
//                    round_listView.setVisibility(View.VISIBLE);
//                    upcomingHeader.setVisibility(View.VISIBLE);
//
//                }
////                editor.putBoolean(SharedPreferenceInfo.ALARMS_SET, false);
////                editor.commit();
////                alarmflag = sharedPreferences.getBoolean(SharedPreferenceInfo.ALARMS_SET, false);
////
////                Log.i("3lama", "changing flag to false Flag " + alarmflag );
////                progressDialog.dismiss();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//                progressDialog.dismiss();
//            }
//        });
//
////        Log.i("3lama", roundTrips.size() +" ----- arraylist size");
////        if (roundTrips.size() < 1){
////            Log.i("3lama", roundTrips.size() +" ----- arraylist size");
////
////            round_listView.setVisibility(View.GONE);
////            upcomingHeader.setVisibility(View.GONE);
////        }
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, 1, 0, "Edit");
        menu.add(0, 2, 1, "Done");
        menu.add(0, 3, 2, "Delete");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int selectedtrip = menuinfo.position; //position in the adapter

        Log.i("3lama", selectedtrip+" ----");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference db = database.getReference("users/" + userID + "/trips");

        switch (item.getItemId()){
            case 1: //Edit
                Intent intent = new Intent(getActivity(), TripAddActivity.class);
                intent.putExtra("selectedTrip", upcomingTrips.get(selectedtrip));
                startActivity(intent);
                break;

            case 2://Done
                Trip markedDone = upcomingTrips.get(selectedtrip);
                //TODO: add the trip to the history arraylist (Can't be found)
                upcomingTrips.remove(selectedtrip);

//                if (upcomingTrips.size() == 0 && roundTrips.size() == 0)
//                {
//                    isEmpty = true;
//                }else{
//                    isEmpty = false;
//                }

                adapter.notifyDataSetChanged();

                confimDoneDialog(markedDone, upcomingTrips);
//                upcomingTrips.remove(selectedtrip);
//                adapter.notifyDataSetChanged();

//                markedDone.setTripStatus(DBConstants.STATUS_DONE);
//                markedDone.setTripDateTime(System.currentTimeMillis());
//                db.child(markedDone.getTripId()).setValue(markedDone);
//                db.child(markedDone.getTripId()).getKey();

//                db.child(markedDone.getTripId()).setValue("Mazaryta");
//                Toast.makeText(getActivity(), "key: " +  db.child(markedDone.getTripId()).getKey(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getActivity(), "Trip Marked Done successfully", Toast.LENGTH_SHORT).show();
                break;

            case 3://Delete
                Trip toDelete = upcomingTrips.get(selectedtrip);
                confimDeleteDialog(toDelete, upcomingTrips);
//                Alert.showConfimDeleteDialog(getActivity(), toDelete);
//                db.child(toDelete.getTripId()).removeValue();
//
//                upcomingTrips.remove(selectedtrip);
//                adapter.notifyDataSetChanged();
//                Toast.makeText(getActivity(), "Trip Deleted Successfully", Toast.LENGTH_SHORT).show();
                break;

        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();


//        if (upcomingTrips.size() == 0 && roundTrips.size() == 0)
//        {
//            isEmpty = true;
//        }else{
//            isEmpty = false;
//        }


        adapter.notifyDataSetChanged();
        roundListAdapter.notifyDataSetChanged();
        ListFormat.setListViewHeightBasedOnChildren(upcoming_listView);
        ListFormat.setListViewHeightBasedOnChildren(round_listView);
    }

    public void confimDeleteDialog(final Trip trip, final ArrayList<Trip> trips) {

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Delete Trip");
        alertDialog.setMessage("Are you sure you want to delete "+trip.getTripTitle()+" ?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Deleting alarm
                TripServices.deleteAlarm(getActivity(), trip);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + SharedPreferenceInfo.getUserId(getApplicationContext()) + "/trips");
                db.child(trip.getTripId()).removeValue();
                trips.remove(trip);
                adapter.notifyDataSetChanged();

            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void confimDoneDialog(final Trip trip, final ArrayList<Trip> trips) {

        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Mark as Done");
        alertDialog.setMessage("Are you sure you want to Mark "+trip.getTripTitle()+" as Done?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Mark as Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Deleting alarm
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + SharedPreferenceInfo.getUserId(getApplicationContext()) + "/trips");

                Trip t = trip;
                t.setTripStatus(DBConstants.STATUS_DONE);
                db.child(trip.getTripId()).setValue(trip);
                trips.remove(trip);
                adapter.notifyDataSetChanged();

            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void refreshData(ArrayList<Trip> upcomingTripsData, ArrayList<Trip> roundTripsData){
        //if (!isEmpty) {
        upcomingTrips.clear();
        roundTrips.clear();
        upcomingTrips.addAll(upcomingTripsData);
        roundTrips.addAll(roundTripsData);


//        if (upcomingTrips.size() == 0 && roundTrips.size() == 0)
//        {
//            isEmpty = true;
//        }else{
//            isEmpty = false;
//        }

        adapter.notifyDataSetChanged();
        roundListAdapter.notifyDataSetChanged();
        ListFormat.setListViewHeightBasedOnChildren(upcoming_listView);
        ListFormat.setListViewHeightBasedOnChildren(round_listView);

        if(roundTrips.size() == 0){
        round_listView.setVisibility(View.GONE);
        upcomingHeader.setVisibility(View.GONE);

        }else {
        round_listView.setVisibility(View.VISIBLE);
        upcomingHeader.setVisibility(View.VISIBLE);

        }
        //}

    }
}
