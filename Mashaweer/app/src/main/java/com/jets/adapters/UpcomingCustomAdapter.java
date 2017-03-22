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
import java.util.Calendar;

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

         ////------//////////// get date and time

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(  trips.get(position).getTripDateTime() );

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR);
        int mMinute = calendar.get(Calendar.MINUTE);


        holder.getTripDate_tv().setText(  mDay + "/" + mMonth);

        if(mHour > 12){
            mHour = mHour - 12;
            holder.getTripTime_tv().setText( mHour + ":"+ mMinute + " P.M."  );
        }else{

            if (mHour < 10)
            {
                if(mMinute <10)
                    holder.getTripTime_tv().setText("0"+ mHour + ":0"+ mMinute + " A.M."  );
                else
                    holder.getTripTime_tv().setText("0"+ mHour + ":"+ mMinute + " A.M."  );
            }
            else{
                if(mMinute <10)
                    holder.getTripTime_tv().setText(mHour + ":0"+ mMinute + " A.M."  );
                else
                    holder.getTripTime_tv().setText(mHour + ":"+ mMinute + " A.M."  );
            }
        }



        ////------///////////////////////////////

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
