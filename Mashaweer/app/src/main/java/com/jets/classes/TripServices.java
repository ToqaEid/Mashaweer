package com.jets.classes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.jets.constants.DBConstants;
import com.jets.classes.Trip;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by toqae on 3/21/2017.
 */

public class TripServices extends Activity{


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i("Tag", "on create TripServices");
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("Tag", "onNewIntent");
        Trip trip = (Trip) intent.getSerializableExtra("trip");
        String className = intent.getStringExtra("methodName");
        boolean nullornot = className == null;
        Log.i("TAG cancel", String.valueOf(nullornot));

        if (className.equals("start")) {
            startTrip(trip);
        }else{
            cancelTrip(trip);
        }
        finish();


    }
    public void cancelTrip(Trip trip){
        Log.i("TAG", "cancel Trip");
        //TODO: set trip status to cancel
        //TODO: update trip in db
    }
    public void startTrip(Trip trip){
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
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ trip.getTripStartLongLat().split(";")[1] + "," + trip.getTripEndLongLat().split(";")[0] +"&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        getApplicationContext().startActivity(mapIntent);
    }

    public static int getTripUniqueId(String tripId){

        MessageDigest digest = null;
        int hash =0;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(tripId.getBytes());
            byte messageDigest[] = digest.digest();
            hash = ByteBuffer.wrap(messageDigest).getInt();
            Log.i("Tag", String.valueOf(hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;
    }

    public static void setAlarm(Context context, Trip trip, long time){

        Intent alarmIntent = new Intent(context, Alarm.class);

        alarmIntent.putExtra("Trip", trip);
        int requestCode = getTripUniqueId(trip.getTripId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), requestCode, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }



}
