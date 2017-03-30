package com.jets.mashaweer;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.jets.classes.Trip;
import com.jets.classes.TripServices;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReminderActivity extends Activity {

    @BindView(R.id.start_btn)
    Button startBtn;
    @BindView(R.id.later_btn)
    Button laterBtn;
    @BindView(R.id.cancel_btn)
    Button cancelBtn;
    @BindView(R.id.reminder_tool)
    Toolbar toolbar;

    private TextView msg;

    private Trip trip;
    private int tripIdInt;


    //Location References
    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private double longitude = 0, latitude = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        this.setFinishOnTouchOutside(false);
        ButterKnife.bind(this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.setActionBar(toolbar);
        }

//        this.setSupportActionBar(toolbar);
        //check if phone is locked, then open the lock and turn screen light on
        Window window = getWindow();
        if(!window.isActive()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }

        setActivitySoundAndVibration();

        //get Trip object from intent
        Intent intent = getIntent();
        trip = (Trip) intent.getSerializableExtra("Trip");

        tripIdInt = TripServices.getTripUniqueId(trip.getTripId());


        ////UI Preparation
        msg = (TextView) findViewById(R.id.msg);
        msg.setText("Time To Start Trip : " +trip.getTripTitle());

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityCompat.requestPermissions(ReminderActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        });
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notifyLater(tripIdInt);
                finish();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TripServices tripServices = new TripServices();
                tripServices.cancelTrip(trip);
                finish();
            }
        });

    }



    ///// for start button GPS permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case 1:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                    getUsersLocation();
                    new TripServices().startTrip(trip);
                    finish();

                } else {
                    // Permission Denied
                    Toast.makeText(ReminderActivity.this, "Sorry, GPS is required to start your trip." , Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    private void getUsersLocation(){
        //////////// 1. check if GPS and [WIFI OR MobileData] are ENABLED
        ////////////////// A. Enabled, then get his current location "XY" and Navigate to Google Maps

        ///////////////// B. NOT Enabled
        ///////////////////////// Ask for a permission to turn them ON

        ///////////////////////////////// Accepted? .... then get his current location "XY" and Navigate to Google Maps

        ///////////////////////////////// Not Accepted? .... Do Nothing; JUST a Toast

        if (ActivityCompat.checkSelfPermission(ReminderActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ReminderActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            ActivityCompat.requestPermissions(ReminderActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationProvider = locationManager.getProvider(locationManager.GPS_PROVIDER);


        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        Log.i("MyTag","Done______________");

        /////////------////////----------/////// test if all permissions are ENABLED

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

            {

                Log.i("MyTag","Done______________I'm in");
            }

            @Override
            public void onLocationChanged(Location location) {


                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.i("MyTag","Done___>>______"+longitude+" ;; " + latitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        notifyLater(tripIdInt);

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        notifyLater(tripIdInt);
//        finish();
//    }

    /*======================== HELPFUL FUNCTIONS ======================================*/
    private void setActivitySoundAndVibration(){
        // vibrate when the activity opens
        Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(500);
        //sound when activity opens
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
        r.play();

    }


    public void notifyLater(int notificationId){

        String START_ACTION = "START_ACTION";
        String CANCEL_ACTION = "CANCEL_ACTION";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ReminderActivity.this);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        builder.setSmallIcon(R.drawable.logo);

        Intent intent = new Intent(this , TripServices.class);
        intent.setAction(START_ACTION);
        intent.putExtra("trip", trip);
        intent.putExtra("notificationId", notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ReminderActivity.this, notificationId, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.addAction(R.drawable.start2, "START", pendingIntent);

        Intent cancelIntent = new Intent(this , TripServices.class);
        cancelIntent.setAction(CANCEL_ACTION);
        cancelIntent.putExtra("trip", trip);
        cancelIntent.putExtra("notificationId", notificationId);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(ReminderActivity.this, notificationId, cancelIntent, 0);
        builder.addAction(R.drawable.cancel1, "CANCEL", cancelPendingIntent);

        builder.setOngoing(true);
        builder.setAutoCancel(true);

        builder.setContentTitle("Mashaweer");
        builder.setContentText(trip.getTripTitle() + " is postponed");
        builder.setSubText("Click to Start Trip Now");

        //set Light and sound of notification
        builder.setLights(0xffffffff, 1000, 200);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Will display the notification in the notification bar
        notificationManager.notify(notificationId, builder.build());

    }

    /*=============================== MENU =====================================*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        new MenuInflater(getApplication()).inflate(R.menu.menu_reminder, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_notes:
                Intent intent = new Intent(ReminderActivity.this, ReminderNotesActivity.class);
                intent.putExtra("trip", trip);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;

            default:
                break;
        }

        return true;
    }
}
