package com.jets.adapters.round;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jets.adapters.notes.uncheckednotes.NotesViewHolder;
import com.jets.classes.Trip;
import com.jets.mashaweer.R;

import java.util.ArrayList;

/**
 * Created by toqae on 3/25/2017.
 */

public class RoundListAdapter extends ArrayAdapter<Trip> {

    private ArrayList<Trip> trips;

    private Context context;

    public RoundListAdapter(Context context, ArrayList<Trip> trips) {
        super(context, R.layout.list_item_round, R.id.round_tripName, trips);
        this.context = context;

        this.trips = trips;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        RoundListViewHolder holder;


        if (rowView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = layoutInflater.inflate(R.layout.list_item_round, parent, false);

            holder = new RoundListViewHolder(rowView);

            rowView.setTag(holder);

        }
        else{

            holder = (RoundListViewHolder) rowView.getTag();
        }


        holder.getRoundName().setText(   trips.get(position).getTripTitle()   );
        holder.getRoundDestination().setText(   trips.get(position).getTripStartLocation()   );


        return  rowView;
    }

    @Override
    public int getCount() {
        Log.i("Tag size", String.valueOf(trips.size()));
        return trips.size();
    }
}

