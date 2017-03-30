package com.jets.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.adapters.DB_Adapter;
import com.jets.adapters.round.RoundListAdapter;
import com.jets.adapters.upcoming.UpcomingCustomAdapter;
import com.jets.classes.ListFormat;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.constants.DBConstants;
import com.jets.constants.SharedPreferenceInfo;
import com.jets.interfaces.Communicator;
import com.jets.mashaweer.R;
import com.jets.mashaweer.TripAddActivity;

import java.util.ArrayList;

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

        Log.i("Test","onCreate UpcomingTripsFragment");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("Test","onCreate UpcomingTripsFragment");

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


        Log.i("Test","onActivityCreated UpcomingTripsFragment");

        communicator = (Communicator) getActivity();
    }
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
                upcomingTrips.remove(selectedtrip);

//                if (upcomingTrips.size() == 0 && roundTrips.size() == 0)
//                {
//                    isEmpty = true;
//                }else{
//                    isEmpty = false;
//                }

                adapter.notifyDataSetChanged();

                confimDoneDialog(markedDone, upcomingTrips);
                break;

            case 3://Delete
                Trip toDelete = upcomingTrips.get(selectedtrip);
                confimDeleteDialog(toDelete, upcomingTrips);
                break;

        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("Test","onResume UpcomingTripsFragment");


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

        Log.i("Test","refreshData UpcomingTripsFragment");

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
