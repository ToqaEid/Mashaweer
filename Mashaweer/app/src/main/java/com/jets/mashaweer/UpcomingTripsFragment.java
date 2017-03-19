package com.jets.mashaweer;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jets.classes.Trip;
import com.jets.classes.UpcomingCustomAdapter;
import com.jets.interfaces.Communicator;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingTripsFragment extends Fragment {

    private ListView upcoming_listView;
    private Communicator communicator;

    private DB_Adapter db_adapter;
    ArrayList<Trip> upcomingTrips;


    boolean isEmpty;

    public UpcomingTripsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db_adapter = new DB_Adapter(getContext());
        upcomingTrips = new ArrayList<>();

//        for (int i=0; i<3; i++)
//        {
//            Trip trip = new Trip();
//            db_adapter.insertTripInfo(trip);
//            Log.i("MyTag","Inserted #" + i);
//        }

        Log.i("MyTag","----------------------------------");
        Cursor cursor = db_adapter.getAllTrips();
        if (cursor.getCount() == 0){
           Log.i("MyTag"," > >  >  >   . . Empty DB");
            isEmpty = true;
        }

       else{

            isEmpty = false;
            while (cursor.moveToNext()){

                Trip trip = new Trip(0,cursor.getInt(8), cursor.getInt(9), cursor.getString(1),
                                    cursor.getString(2), cursor.getString(3), cursor.getString(4),
                                    cursor.getString(5),cursor.getString(6), cursor.getString(7));

                Log.i("MyTag","-=-= Obj # " +trip);
                upcomingTrips.add(trip);
            }

            Log.i("MyTag"," > >  >  >  Size of tripsArray " + upcomingTrips.size());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i("MyTag","OnCreateView");
        View rootView = null;
        if (! isEmpty)
        {
             rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);

            upcoming_listView = (ListView)  rootView.findViewById(R.id.upcoming_listView);

            UpcomingCustomAdapter adapter = new UpcomingCustomAdapter(getContext(),upcomingTrips );
            upcoming_listView.setAdapter(adapter);
            Log.i("MyTag","Upcoming adapter is set");
            upcoming_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Toast.makeText(getActivity(),  "Position = " + position, Toast.LENGTH_SHORT).show();

                    Trip selectedTrip = upcomingTrips.get(position);

                    communicator.sendMsg(selectedTrip);
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
}
