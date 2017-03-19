package com.jets.classes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jets.classes.Trip;
import com.jets.mashaweer.R;
import com.jets.mashaweer.UpcomingListViewHolder;

import java.util.ArrayList;

/**
 * Created by mohamed on 04/03/2017.
 */

public class UpcomingCustomAdapter extends ArrayAdapter<Trip> {

    private ArrayList<Trip> trips;

    private Context context;

    public UpcomingCustomAdapter(Context context, ArrayList<Trip> trips) {
        super(context, R.layout.list_item_upcoming_library, R.id.upcoming_tripName, trips);
        this.context = context;

        this.trips = trips;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        UpcomingListViewHolder holder;


        if (rowView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = layoutInflater.inflate(R.layout.list_item_upcoming_library, parent, false);

            holder = new UpcomingListViewHolder(rowView);

            rowView.setTag(holder);

        }
        else{

                holder = (UpcomingListViewHolder) rowView.getTag();
        }


        holder.getTripName_tv().setText(   trips.get(position).getTripTitle()   );
        holder.getTripDate_tv().setText(  (trips.get(position).getTripDateTime().split(" ")[0]).split("/")[0] +"/"+ (trips.get(position).getTripDateTime().split(" ")[0]).split("/")[1]  );
        holder.getTripTime_tv().setText(    trips.get(position).getTripDateTime().split(" ")[1]  );
        holder.getTripType_tv().setText(  trips.get(position).getTripType() + ""   );

        return  rowView;
    }


}
