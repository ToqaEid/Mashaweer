package com.jets.mashaweer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mohamed on 04/03/2017.
 */

public class UpcomingCustomAdapter extends ArrayAdapter<String> {

    private String [] tripNames;
    private String [] tripDatesAndTime;
    private String [] RoundOrNot;

    private Context context;

    public UpcomingCustomAdapter(Context context, String [] tripNames, String [] tripDatesAndTime, String [] RoundOrNot) {
        super(context, R.layout.list_item_upcoming, R.id.upcoming_tripName, tripNames);
        this.context = context;
        this.tripNames = tripNames;
        this.tripDatesAndTime = tripDatesAndTime;
        this.RoundOrNot = RoundOrNot;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.list_item_upcoming, parent, false);

        TextView tv_tripName = (TextView) view.findViewById(R.id.upcoming_tripName);
        TextView tv_tripDate = (TextView) view.findViewById(R.id.upcoming_tripDate);
        TextView tv_tripTime = (TextView) view.findViewById(R.id.upcoming_tripTime);
        TextView tv_tripType = (TextView) view.findViewById(R.id.upcoming_tripType);

        tv_tripName.setText(   tripNames[position]   );
        tv_tripDate.setText(  tripDatesAndTime[position].split(",")[1]   );
        tv_tripTime.setText(    tripDatesAndTime[position].split(",")[0]   );
        tv_tripType.setText(  RoundOrNot[position]   );

        return  view;
    }


}
