package com.jets.classes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.constants.DBConstants;
import com.jets.classes.Trip;
import com.jets.constants.SharedPreferenceInfo;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by toqae on 3/21/2017.
 */

public class TripServices extends BroadcastReceiver{

    private Trip tripObj;
    DatabaseReference db;
    String userID = SharedPreferenceInfo.getUserId(getApplicationContext());

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        tripObj = (Trip) intent.getSerializableExtra("trip");
        int notificationId = intent.getIntExtra("notificationId", 0 );

        String START_ACTION = "START_ACTION";
        String CANCEL_ACTION = "CANCEL_ACTION";
        if(action.equals(START_ACTION)) {
            startTrip(tripObj);
        } else if(action.equals(CANCEL_ACTION)){
            cancelTrip(tripObj);
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }

    public void cancelTrip(Trip trip){
        Log.i("TAG", "cancel Trip");
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = database.getReference("users/" + userID + "/trips");

        trip.setTripStatus(DBConstants.STATUS_CANCELLED);

        db.child(trip.getTripId()).setValue(trip);
    }

    public void startTrip(Trip trip){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = database.getReference("users/" + userID + "/trips");

        if(trip.getTripType() == DBConstants.TYPE_ONE_WAY){

            trip.setTripStatus(DBConstants.STATUS_DONE);

        }else{
            if (trip.getTripStatus() == DBConstants.STATUS_PENDING){
                //status pending trip
                trip.setTripStatus(DBConstants.STATUS_PENDING);

            }else{
                //upcoming trip
                trip.setTripStatus(DBConstants.STATUS_DONE);
            }
        }

        trip.setTripDateTime(System.currentTimeMillis());
        db.child(trip.getTripId()).setValue(trip);
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ trip.getTripEndLongLat() +"&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mapIntent.setPackage("com.google.android.apps.maps");
        getApplicationContext().startActivity(mapIntent);
    }

    /***
     * Function getTripUniqueId
     * used for generating unique integer from Unique Trip Id String
     * uses concept of hashing in security
     ***/
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

        Log.i("3lama", trip.getTripTitle());

        Intent alarmIntent = new Intent(context, Alarm.class);

        alarmIntent.putExtra("Trip", trip);
        int requestCode = getTripUniqueId(trip.getTripId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), requestCode, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }



}
