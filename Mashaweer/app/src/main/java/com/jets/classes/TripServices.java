package com.jets.classes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.jets.constants.DBConstants;

/**
 * Created by toqae on 3/21/2017.
 */

public class TripServices {

    public void startTrip(Context context, Trip trip){
        if(trip.getTripType() == DBConstants.TYPE_ONE_WAY){
            //TODO: UPDATE DATABASE WITHT HE NEW OBJECT
        }else{
            if (trip.getTripStatus() == DBConstants.STATUS_PENDING){
                //TODO: update database with the new object
                //sstatus pendin trip
            }else{
                //TODO: update database with the new object
                //upcoing trip
            }
            //not round trip
        }
        //TODO: update the date of trip to be now date
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ trip.getTripEndLong().split(";")[1] + "," + trip.getTripEndLong().split(";")[0] +"&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.getApplicationContext().startActivity(mapIntent);
    }



}
