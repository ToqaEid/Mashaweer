package com.jets.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.jets.classes.ListFormat;
import com.jets.constants.DBConstants;
import com.jets.fragments.UpcomingTripsFragment;
import com.jets.mashaweer.R;
import com.jets.classes.UpcomingListViewHolder;
import com.jets.classes.Trip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by mohamed on 04/03/2017.
 */

public class UpcomingCustomAdapter extends ArrayAdapter<Trip> implements  GoogleApiClient.OnConnectionFailedListener {

    private ArrayList<Trip> trips;

    private Context context;


    //UpcomingListViewHolder holder;
    private GoogleApiClient mGoogleApiClient;
    Bitmap bitmap = null;


    public UpcomingCustomAdapter(Context context, ArrayList<Trip> trips) {
        super(context, R.layout.list_item_upcoming_library, R.id.upcoming_tripName, trips);
        this.context = context;

        this.trips = trips;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        final UpcomingListViewHolder holder;


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
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
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
            case DBConstants.TYPE_ONE_WAY:
                holder.getTripType_tv().setVisibility(View.GONE);
                break;
            case DBConstants.TYPE_ROUND_TRIP:
                holder.getTripType_tv().setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }


        Bitmap img = loadImageFromStorage( context.getFilesDir().getAbsolutePath() , trips.get(position).getTripPlaceId());

        Log.i("MyTag","Back from internal storage");

        if (img != null)
            holder.getTripThumbnail().setImageBitmap( img );
//        else {
//            img = BitmapFactory.decodeResource(context.getResources(), R.drawable.trip2);   ///// default image
//            holder.getTripThumbnail().setImageBitmap(img);
//        }

//

        return  rowView;
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




    @Override
    public int getCount() {
        Log.i("Tag size", String.valueOf(trips.size()));
        return trips.size();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, "Connecion Lost >> " + connectionResult.getErrorMessage() , Toast.LENGTH_SHORT).show();

    }



}
