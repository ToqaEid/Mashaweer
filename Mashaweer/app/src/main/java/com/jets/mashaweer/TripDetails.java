package com.jets.mashaweer;

import android.*;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jets.classes.Alarm;
import com.jets.classes.Trip;

import org.w3c.dom.Text;

public class TripDetails extends AppCompatActivity {

    Intent previousIntent;

    private TextView tv_tripStatus, tv_tripFrom, tv_tripTo, tv_tripDate, tv_tripTime_1, tv_tripTime_2;

    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private double longitude=0, latitude=0;

    String tripName = "TripOne";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details2);

        previousIntent = getIntent();
        final Trip trip = (Trip) previousIntent.getSerializableExtra("selectedTrip");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle( trip.getTripTitle() );
        setSupportActionBar(toolbar);

        //////////////////  egt references for TextViews

        tv_tripStatus = (TextView) findViewById(R.id.tripDetails_tv_status);
        tv_tripDate = (TextView) findViewById(R.id.tripDetails_tv_date);
        tv_tripFrom = (TextView) findViewById(R.id.tripDetails_tv_from);
        tv_tripTo = (TextView) findViewById(R.id.tripDetails_tv_to);
        tv_tripTime_1 = (TextView) findViewById(R.id.tripDetails_tv_time1);
        tv_tripTime_2 = (TextView) findViewById(R.id.tripDetails_tv_time2);

        /////////////// populate data in tripVeiw

        if ( trip.getTripStatus() == 1)
            tv_tripStatus.setText("UpComing Trip");
        else
            tv_tripStatus.setText("Done Trip");

        tv_tripFrom.setText( trip.getTripStartLat() );
        tv_tripTo.setText( trip.getTripEndLAt() );
        tv_tripDate.setText( trip.getTripDateTime().split(" ")[0] );
        tv_tripTime_1.setText( trip.getTripDateTime().split(" ")[1] );

        if ( Integer.parseInt( tv_tripTime_1.getText().toString().split(":")[0]) > 12 )
            tv_tripTime_2.setText("PM");
        else
        {
            tv_tripTime_2.setText("AM");
        }
        //////////////// handling buttons' click listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.edit_floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TripDetails.this, TripAddActivity.class);
                startActivity(intent);

                Toast.makeText(TripDetails.this, "---End of EDIT-Trip btnClick---", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(TripDetails.this, Alarm.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                        TripDetails.this.getApplicationContext(), 234324243, intent, 0);
//                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                        + (3*1000), pendingIntent);
//                Toast.makeText(TripDetails.this, "Alarm will fire in 3 seconds",Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fab_play = (FloatingActionButton) findViewById(R.id.play_floating_button);
        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //////////// 1. check if GPS and [WIFI OR MobileData] are ENABLED
                ////////////////// A. Enabled, then get his current location "XY" and Navigate to Google Maps

                ///////////////// B. NOT Enabled
                ///////////////////////// Ask for a permission to turn them ON

                ///////////////////////////////// Accepted? .... then get his current location "XY" and Navigate to Google Maps

                ///////////////////////////////// Not Accepted? .... Do Nothing; JUST a Toast

                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationProvider = locationManager.getProvider(locationManager.GPS_PROVIDER);


                boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!enabled) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }



                if (ActivityCompat.checkSelfPermission(TripDetails.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(TripDetails.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.


                    ActivityCompat.requestPermissions(TripDetails.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    return;
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


//                /////////------////////---------//////////////////////////////////////////////

                Toast.makeText(TripDetails.this, "---End of START btnClick---", Toast.LENGTH_SHORT).show();

                Uri gmmIntentUri = Uri.parse("google.navigation:q="+ trip.getTripEndLong().split(";")[1] + "," + trip.getTripEndLong().split(";")[0] +"&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });
    }
    /*====================== MENU ==============================*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_trip_details, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteTrip();
                return true;
            case R.id.action_done:
                doneTrip();
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doneTrip() {
        Toast.makeText(this, "done trip", Toast.LENGTH_SHORT).show();
    }

    private void deleteTrip() {
        Toast.makeText(this, "delete trip", Toast.LENGTH_SHORT).show();
    }
    /*==== END MENU ===*/
}
