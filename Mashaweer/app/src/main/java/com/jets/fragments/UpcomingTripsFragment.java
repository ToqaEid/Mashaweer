package com.jets.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingTripsFragment extends Fragment {

    private ListView upcoming_listView;
    private ListView round_listView;
    private Communicator communicator;
    private UpcomingCustomAdapter adapter;
    private RoundListAdapter roundListAdapter;
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


//        if (upcomingTrips.size() == 0){
//           Log.i("MyTag"," > >  >  >   . . Empty DB");
//            isEmpty = true;
//        }else {
//            isEmpty = false;
//        }



//
//        db_adapter = new DB_Adapter(getContext());
//        upcomingTrips = new ArrayList<>();
//
////        for (int i=0; i<3; i++)
////        {
////            Trip trip = new Trip();
////            db_adapter.insertTripInfo(trip);
////            Log.i("MyTag","Inserted #" + i);
////        }
//
//        Log.i("MyTag","----------------------------------");
//        Cursor cursor = db_adapter.getAllTrips();
//        if (cursor.getCount() == 0){
//           Log.i("MyTag"," > >  >  >   . . Empty DB");
//            isEmpty = true;
//        }
//
//       else{
//
//            isEmpty = false;
//            while (cursor.moveToNext()){
//
//                Trip trip = new Trip(0,cursor.getInt(8), cursor.getInt(9), cursor.getString(1),
//                                    cursor.getString(2), cursor.getString(3), cursor.getString(4),
//                                    cursor.getString(5),cursor.getString(6), cursor.getString(7));
//
//                Log.i("MyTag","-=-= Obj # " +trip);
//                upcomingTrips.add(trip);
//            }
//
//            Log.i("MyTag"," > >  >  >  Size of tripsArray " + upcomingTrips.size());
//        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        if (! isEmpty)
        {
             rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);

            roundTitle = (TextView) rootView.findViewById(R.id.round_title);

            upcoming_listView = (ListView)  rootView.findViewById(R.id.upcoming_listView);

            round_listView = (ListView) rootView.findViewById(R.id.round_listView);

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

                    Toast.makeText(getActivity(),  "Position = " + position, Toast.LENGTH_SHORT).show();

                    Trip selectedTrip = upcomingTrips.get(position);

                    communicator.sendMsg(selectedTrip);
                }
            });
            upcoming_listView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return false;
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

    @Override
    public void onStart() {
        super.onStart();


        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Dark_Dialog);// TODO: add appTheme_Dark_Dialog theme
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        // reading and updating data from database
        userID = SharedPreferenceInfo.getUserId(getActivity());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference db = database.getReference("users/" + userID);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                upcomingTrips.clear();
                roundTrips.clear();
                //TODO: clear all the other lists as well

                Iterable<DataSnapshot> trips = dataSnapshot.child("trips").getChildren();

                while (trips.iterator().hasNext()) {
                    DataSnapshot returnedData = trips.iterator().next();
                    Trip trip = returnedData.getValue(Trip.class);
                    trip.setTripId(returnedData.getKey());

                    switch (trip.getTripStatus()) {

                        case DBConstants.STATUS_UPCOMING:
                            upcomingTrips.add(trip);
                            break;

                        case DBConstants.STATUS_PENDING:
                            roundTrips.add(trip);
                            break;

                        default:
                            //TODO: Replace the array list "upcoming trips" with the done/cancelled arraylist name
                            upcomingTrips.add(trip);
                            break;
                    }

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                        roundListAdapter.notifyDataSetChanged();
                        ListFormat.setListViewHeightBasedOnChildren(upcoming_listView);
                        ListFormat.setListViewHeightBasedOnChildren(round_listView);
                    }

                }

                progressDialog.dismiss();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });
        if(roundTrips.size() == 0){
            round_listView.setVisibility(View.GONE);
            roundTitle.setVisibility(View.GONE);

        }else {
            round_listView.setVisibility(View.VISIBLE);
            roundTitle.setVisibility(View.VISIBLE);

        }
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
                //TODO: add the trip to the history arraylist (Can't be found)
                upcomingTrips.remove(selectedtrip);
                adapter.notifyDataSetChanged();

                markedDone.setTripStatus(DBConstants.STATUS_DONE);
                markedDone.setTripDateTime(System.currentTimeMillis());
                db.child(markedDone.getTripId()).setValue(markedDone);

                Toast.makeText(getActivity(), "Trip Marked Done successfully", Toast.LENGTH_SHORT).show();
                break;

            case 3://Delete
                Trip toDelete = upcomingTrips.get(selectedtrip);
                db.child(toDelete.getTripId()).removeValue();

                upcomingTrips.remove(selectedtrip);
                //TODO: Notify the adapter to update the listview
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Trip Deleted Successfully", Toast.LENGTH_SHORT).show();
                break;

        }

        return super.onContextItemSelected(item);
    }
}
