package com.jets.mashaweer;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingTripsFragment extends Fragment {

    private ListView upcoming_listView;

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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);

        upcoming_listView = (ListView)  rootView.findViewById(R.id.upcoming_listView);

        String [] tripNames = {
                "To Alex",
                "To Giza",
                "To Aswan",
                "To Luxor",
                "To Sinai",
                "To Alex",
                "To Port said",
                "To Tanta",
                "To Domyatt"
        };

        String [] tripDatesTimes = {
                "11:45,22/4",
                "23:30,1/1",
                "02:30,11/8",
                "07:15,3/4",
                "11:00,2/9",
                "09:09,10/10",
                "04:04,3/1",
                "05:45,12/2",
                "18:20,5/10"
        };

        String [] tripRoundType = {
                "*Round trip",
                "*One way",
                "*Round trip",
                "*Round trip",
                "*Round trip",
                "*One way",
                "*Round trip",
                "*One way",
                "*One way"
        };


        UpcomingCustomAdapter adapter = new UpcomingCustomAdapter(getContext(),tripNames, tripDatesTimes, tripRoundType );
        upcoming_listView.setAdapter(adapter);

        upcoming_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              //  Toast.makeText(MainActivity.this,  upcoming_listView.getAdapter().getItem(position).toString() , Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }

}
