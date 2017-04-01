package com.jets.classes;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.constants.DBConstants;
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
    private DatabaseReference db;
    private String userID = SharedPreferenceInfo.getUserId(getApplicationContext());

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        tripObj = (Trip) intent.getSerializableExtra("trip");


        Log.i("MyTag", "onReceive >> tripType >> " + tripObj.getTripType());
        Log.i("MyTag", "onReceive >> tripStatus >> " + tripObj.getTripStatus());


        int notificationId = intent.getIntExtra("notificationId", 0 );

        String START_ACTION = "START_ACTION";
        String CANCEL_ACTION = "CANCEL_ACTION";

        if(action.equals(START_ACTION)) {


            Log.i("MyTag", "onReceive StartBTN >> tripType >> " + tripObj.getTripType());
            Log.i("MyTag", "onReceive StartBTN >> tripStatus >> " + tripObj.getTripStatus());

            startTrip(tripObj);
        } else if(action.equals(CANCEL_ACTION)){
            cancelTrip(tripObj);
        }
        Log.i("Tag act", action);
        Log.i("Tag not", notificationId +"");
        //remove notification from notification drawer
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
        //collapse notification drawer
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void cancelTrip(Trip trip){
        Log.i("TAG", "cancel Trip");
        //delete form db
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = database.getReference("users/" + userID + "/trips");
        trip.setTripStatus(DBConstants.STATUS_CANCELLED);
        db.child(trip.getTripId()).setValue(trip);

        deleteAlarm(getApplicationContext(), trip);
    }

    public void startTrip(Trip trip){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = database.getReference("users/" + userID + "/trips");
        String endLongLat;

        Log.i("Tag start", "type "+ trip.getTripType());

        Log.i("MyTag","TripServices  _  startTripFunc:: TripType >> " + trip.getTripType());

        if(trip.getTripType() == DBConstants.TYPE_ONE_WAY){
            endLongLat = trip.getTripEndLongLat();
            Log.i("Tag start", "here "+ endLongLat);
            trip.setTripStatus(DBConstants.STATUS_DONE);

        }else{      //type == round
            if (trip.getTripStatus() == DBConstants.STATUS_UPCOMING){
                //status pending trip
                endLongLat = trip.getTripEndLongLat();
                trip.setTripStatus(DBConstants.STATUS_PENDING);

            }else{
                //upcoming trip
                endLongLat = trip.getTripStartLocation();
                trip.setTripStatus(DBConstants.STATUS_DONE);
            }
        }

        trip.setTripDateTime(System.currentTimeMillis());

        //add to db
        db.child(trip.getTripId()).setValue(trip);
        //open map
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ endLongLat +"&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mapIntent.setPackage("com.google.android.apps.maps");
        getApplicationContext().startActivity(mapIntent);
        //delete alarm after starting
        deleteAlarm(getApplicationContext(), trip);
        //delete notification
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(getTripUniqueId(trip.getTripId()));
        //collapse notification drawer
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

    public static void setAlarm(Context context, Trip trip){

        Log.i("3lama2", trip.getTripTitle());

        Intent alarmIntent = new Intent(context, Alarm.class);

        alarmIntent.putExtra("Trip", trip);

        Log.i("MyTag","TripServices :: _ setAlarmFunc TripType >> " + trip.getTripType());
        Log.i("MyTag","TripServices :: _ setAlarmFunc TripStatus >> " + trip.getTripStatus());

        int requestCode = getTripUniqueId(trip.getTripId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, trip.getTripDateTime(), pendingIntent);
    }

    public static void updateAlarm(Context context, Trip trip){

        Log.i("3lama", trip.getTripTitle());

        Intent alarmIntent = new Intent(context, Alarm.class);

        alarmIntent.putExtra("Trip", trip);
        int requestCode = getTripUniqueId(trip.getTripId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, trip.getTripDateTime(), pendingIntent);
    }

    public static void deleteAlarm(Context context, Trip trip){

        Log.i("3lama", trip.getTripTitle() + " Cancellingggggggg");

        Intent alarmIntent = new Intent(context, Alarm.class);

        alarmIntent.putExtra("Trip", trip);
        int requestCode = getTripUniqueId(trip.getTripId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), requestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }



    //////// get month name from month number
    public static String getMonthName (int monthNumber)
    {
        //TODO: Converting month number to its name to be good for UI in trip info.

        switch (monthNumber)
        {
            case 1 :
                return "January";

            case 2 :
                return "February";

            case 3 :
                return "March";

            case 4 :
                return "April";

            case 5 :
                return "May";

            case 6 :
                return "June";

            case 7 :
                return "July";

            case 8 :
                return "August";

            case 9 :
                return "September";

            case 10 :
                return "November";

            case 11 :
                return "December";

            case 12 :
                return "January";

            default:
                return "Invalid Month Number!";

        }

    }



}
