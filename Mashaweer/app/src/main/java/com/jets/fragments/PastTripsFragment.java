package com.jets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.jets.adapters.UpcomingCustomAdapter;
import com.jets.classes.ListFormat;
import com.jets.classes.Trip;
import com.jets.interfaces.Communicator;
import com.jets.mashaweer.DB_Adapter;
import com.jets.mashaweer.R;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PastTripsFragment extends Fragment {

    private ListView past_listView;
    private Communicator communicator;
    private UpcomingCustomAdapter adapter;

    private DB_Adapter db_adapter;
    public static ArrayList<Trip> pastTrips = new ArrayList<>();
    private String userID;

    boolean isEmpty;

    public PastTripsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
      //  if (! isEmpty)
      //  {
            rootView = inflater.inflate(R.layout.fragment_past, container, false);

            past_listView = (ListView)  rootView.findViewById(R.id.past_listView);

            adapter = new UpcomingCustomAdapter(getActivity(),pastTrips );

            adapter.notifyDataSetChanged();

            past_listView.setAdapter(adapter);
//            registerForContextMenu(past_listView);

            past_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Trip selectedTrip = pastTrips.get(position);
                    communicator.sendMsg(selectedTrip);
                }
            });
            past_listView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return false;
                }
            });

//        }else{
//
//            rootView = inflater.inflate(R.layout.empty_listview, container, false);
//
//        }

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        communicator = (Communicator) getActivity();
    }


//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//
//        menu.add(0, 1, 0, "Delete");
//
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//
//        AdapterView.AdapterContextMenuInfo menuinfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
//        int selectedtrip = menuinfo.position; //position in the adapter
//
//        Log.i("3lama", selectedtrip+" ----");
//
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference db = database.getReference("users/" + userID + "/trips");
//
//        switch (item.getItemId()){
//
//            case 1://Delete
//                Trip toDelete = pastTrips.get(selectedtrip);
//                db.child(toDelete.getTripId()).removeValue();
//
//                pastTrips.remove(selectedtrip);
//                //TODO: Notify the adapter to update the listview
//                adapter.notifyDataSetChanged();
//                Toast.makeText(getActivity(), "Trip Deleted Successfully", Toast.LENGTH_SHORT).show();
//                break;
//
//        }
//
//        return super.onContextItemSelected(item);
//    }

    public void refreshData(ArrayList<Trip> tripsData){
        //if (!isEmpty) {
            pastTrips.clear();
            pastTrips.addAll(tripsData);

            adapter.notifyDataSetChanged();
            ListFormat.setListViewHeightBasedOnChildren(past_listView);
        //}

    }

}
