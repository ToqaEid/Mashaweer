package com.jets.adapters.round;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jets.adapters.notes.uncheckednotes.NotesViewHolder;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.mashaweer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by toqae on 3/25/2017.
 */

public class RoundListAdapter extends ArrayAdapter<Trip> {

    private ArrayList<Trip> trips;
    private TripServices tripServices;
    private Context context;

    public RoundListAdapter(Context context, ArrayList<Trip> trips) {
        super(context, R.layout.list_item_round, R.id.round_tripName, trips);
        this.context = context;

        this.trips = trips;

        tripServices = new TripServices();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

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


        if(trips.get(position).getTripTitle().length() > 20){
            holder.getRoundName().setText(   trips.get(position).getTripTitle().subSequence(0, 19)+ "..."   );
        }else{
            holder.getRoundName().setText(   trips.get(position).getTripTitle()   );
        }
        if(trips.get(position).getTripTitle().length() > 20){
            holder.getRoundDestination().setText(   trips.get(position).getTripStartLocation() .subSequence(0, 19)+ "..."   );
        }else{
            holder.getRoundDestination().setText(   trips.get(position).getTripStartLocation()   );
        }

        holder.getStartTrip().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tripServices.startTrip(trips.get(position));
            }
        });

        Bitmap img = loadImageFromStorage( context.getFilesDir().getAbsolutePath() , trips.get(position).getTripPlaceId());

        Log.i("MyTag","Back from internal storage");

        if (img != null)
            holder.getImageView().setImageBitmap( img );

        return  rowView;
    }

    @Override
    public int getCount() {
        Log.i("Tag size", String.valueOf(trips.size()));
        return trips.size();
    }

    ////////////// load image from internal storage
    private Bitmap loadImageFromStorage(String path, String placeId)
    {
        Bitmap b = null;

        Log.i("MyTag","Loading image from internal storage ... ");

        try {

            ContextWrapper cw = new ContextWrapper(getApplicationContext());

            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

            // Create imageDir
            File f=new File(directory.getAbsolutePath(), placeId + ".jpg");


            //File f=new File(path+"/", placeId+".jpg");
            Log.i("MyTag","Image Path .. " + directory.getAbsolutePath() + "/" +  placeId+".jpg");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
            Log.i("MyTag","Image successfully found");
            return  b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Log.i("MyTag","Image not found");
        return b;
    }
}

