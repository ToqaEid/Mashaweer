package com.jets.adapters;

import android.content.Context;
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
import com.jets.fragments.UpcomingTripsFragment;
import com.jets.mashaweer.R;
import com.jets.classes.UpcomingListViewHolder;
import com.jets.classes.Trip;

import java.util.ArrayList;
import java.util.Calendar;

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


        ///////////// handler

         final Handler messageHandler = new Handler() {

            public void handleMessage(Message msg) {

                super.handleMessage(msg);

                Log.i("MyTag" , "Inside handler for trip# " + position);

                if ( msg.what == 0 ) {
                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.trip2);   ///// default image
                    holder.getTripThumbnail().setImageBitmap(bitmap);
                }
                else {
                    holder.getTripThumbnail().setImageBitmap(bitmap);
                }
            }
        };

        //////////////////////////// get Image

        new Thread() {
            public void run() {

                Log.i("MyTag" , "Thread is running ....");

                //bitmap = downloadBitmap("ChIJdd4hrwug2EcRmSrV3Vo6llI");
                Log.i("MyTag" , "Pos# " + position + " ..... placeId = " + trips.get(position).getTripPlaceId());
                bitmap = downloadBitmap(trips.get(position).getTripPlaceId());

                if (bitmap == null)
                    messageHandler.sendEmptyMessage(0);
                else
                    messageHandler.sendEmptyMessage(1);
            }
        }.start();

        ///////////////////////////////////////

        return  rowView;
    }

    @Override
    public int getCount() {
        Log.i("Tag size", String.valueOf(trips.size()));
        return trips.size();
    }
    private Bitmap downloadBitmap(String url) {

        Log.i("MyTag" , "Downloading Image now ...  ");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                   // .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Places.GEO_DATA_API)
                    .build();

            mGoogleApiClient.connect();
        } else if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
        Log.i("MyTag" , "???? ");

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, url).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0) {
                // Get the first bitmap and its attributions.

                int rand = (int) (Math.floor(Math.random()) * photoMetadataBuffer.getCount());

                Log.i("MyTag", "#of photos : " + photoMetadataBuffer.getCount());

                PlacePhotoMetadata photo = photoMetadataBuffer.get(rand);
                CharSequence attribution = photo.getAttributions();
                // Load a scaled bitmap for this photo.
                bitmap = photo.getScaledPhoto(mGoogleApiClient, 500, 500).await()
                        .getBitmap();
            }
            else{
                bitmap = null;
            }
            // Release the PlacePhotoMetadataBuffer.
            photoMetadataBuffer.release();
        }

        return bitmap;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, "Connecion Lost >> " + connectionResult.getErrorMessage() , Toast.LENGTH_SHORT).show();

    }

}
