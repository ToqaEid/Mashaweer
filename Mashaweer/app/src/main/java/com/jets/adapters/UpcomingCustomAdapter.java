package com.jets.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jets.activites.R;
import com.jets.classes.UpcomingListViewHolder;
import com.jets.classes.Trip;

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
        holder.getTripDate_tv().setText(  (trips.get(position).getTripDateTime().split(" ")[0]).split("/")[0] +"/"+ (trips.get(position).getTripDateTime().split(" ")[0]).split("/")[1]
                                            +"/"+ (trips.get(position).getTripDateTime().split(" ")[0]).split("/")[2]);
        int time = (Integer.parseInt(trips.get(position).getTripDateTime().split(" ")[1].split(":")[0]));
        if(time > 12){
            time = time - 12;
            holder.getTripTime_tv().setText( String.valueOf(time) + ":"+ trips.get(position).getTripDateTime().split(" ")[1].split(":")[1]+ " P.M."  );
        }else{
            holder.getTripTime_tv().setText( trips.get(position).getTripDateTime().split(" ")[1] + " A.M."  );
        }

        int tripType = trips.get(position).getTripType();
        switch(tripType){
            case 0:
                holder.getTripType_tv().setText( "" );
                break;
            case 1:
                holder.getTripType_tv().setText( "Round Trip" );
                break;
            default:
                break;
        }


        return  rowView;
    }


}
