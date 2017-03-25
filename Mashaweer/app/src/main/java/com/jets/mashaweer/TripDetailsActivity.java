package com.jets.mashaweer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.constants.Alert;
import com.jets.constants.DBConstants;
import com.jets.constants.SharedPreferenceInfo;

import java.util.Calendar;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TripDetailsActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {

    Intent previousIntent;

    private TextView tv_tripStatus, tv_tripFrom, tv_tripTo, tv_tripDate, tv_tripTime_1, tv_tripTime_2;

    private ImageView imageViewTrip;

    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private double longitude=0, latitude=0;

    private Trip trip;
    String tripName = "TripOne";

    private GoogleApiClient mGoogleApiClient;
    Bitmap bitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details2);

        previousIntent = getIntent();
        trip = (Trip) previousIntent.getSerializableExtra("selectedTrip");



        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(trip.getTripId().getBytes());
            byte messageDigest[] = digest.digest();
            int hash = ByteBuffer.wrap(messageDigest).getInt();
            Log.i("Tag", String.valueOf(hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

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

        imageViewTrip = (ImageView) findViewById(R.id.tripDetails_Image);

        /////////////// populate data in tripVeiw

        if ( trip.getTripStatus() == 1)
            tv_tripStatus.setText("UpComing Trip");
        else
            tv_tripStatus.setText("Done Trip");

        tv_tripFrom.setText( trip.getTripStartLocation() );
        tv_tripTo.setText( trip.getTripEndLocation() );



        /////// get date and time from milliseconds

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( trip.getTripDateTime() );

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        int mHour = calendar.get(Calendar.HOUR);
        int mMinute = calendar.get(Calendar.MINUTE);

        tv_tripDate.setText( mDay + " / " + mMonth );

        if ( mHour >= 12 )
        {
            tv_tripTime_1.setText( (mHour-12) + "");
            tv_tripTime_2.setText("PM");

        }else
        {
            tv_tripTime_1.setText( mHour + "");
            tv_tripTime_2.setText("AM");
        }

        //////////////// handling buttons' click listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.edit_floating_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TripDetailsActivity.this, TripAddActivity.class);
                intent.putExtra("selectedTrip", trip);
                startActivity(intent);

                Toast.makeText(TripDetailsActivity.this, "---End of EDIT-Trip btnClick---", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(TripDetailsActivity.this, Alarm.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                        TripDetailsActivity.this.getApplicationContext(), 234324243, intent, 0);
//                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                        + (3*1000), pendingIntent);
//                Toast.makeText(TripDetailsActivity.this, "Alarm will fire in 3 seconds",Toast.LENGTH_LONG).show();
            }
        });

        FloatingActionButton fab_play = (FloatingActionButton) findViewById(R.id.play_floating_button);
        Log.i("invi", String.valueOf(trip.getTripStatus()));
        if(trip.getTripStatus() == DBConstants.STATUS_DONE){
            Log.i("Tag","invisible");
            fab.setVisibility(View.INVISIBLE);
            fab_play.setVisibility(View.GONE);

        }
        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //////////// 1. check if GPS and [WIFI OR MobileData] are ENABLED
                ////////////////// A. Enabled, then get his current location "XY" and Navigate to Google Maps

                ///////////////// B. NOT Enabled
                ///////////////////////// Ask for a permission to turn them ON

                ///////////////////////////////// Accepted? .... then get his current location "XY" and Navigate to Google Maps

                ///////////////////////////////// Not Accepted? .... Do Nothing; JUST a Toast




                if (ActivityCompat.checkSelfPermission(TripDetailsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(TripDetailsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.


                    ActivityCompat.requestPermissions(TripDetailsActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    return;
                }
                Log.i("MyTag","Done______________");




                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationProvider = locationManager.getProvider(locationManager.GPS_PROVIDER);


                boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!enabled) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }


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

                Toast.makeText(TripDetailsActivity.this, "---End of START btnClick---", Toast.LENGTH_SHORT).show();

                new TripServices().startTrip(trip);
//                Uri gmmIntentUri = Uri.parse("google.navigation:q="+ trip.getTripEndLong().split(";")[1] + "," + trip.getTripEndLong().split(";")[0] +"&mode=d");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);

            }
        });


        //////////////////// get place image

        ///////////// handler

        final Handler messageHandler = new Handler() {

            public void handleMessage(Message msg) {

                super.handleMessage(msg);

                Log.i("MyTag" , "Inside handler for trip# " + trip.getTripTitle());

                if ( msg.what == 0 ) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.trip2);
                    imageViewTrip.setImageBitmap(bitmap);
                }
                else {
                    imageViewTrip.setImageBitmap(bitmap);
                }
            }
        };


        //////////////////////////// get Image

        new Thread() {
            public void run() {

                Log.i("MyTag" , "Thread is running ....");

                bitmap = downloadBitmap(trip.getTripPlaceId());

                if (bitmap == null)
                    messageHandler.sendEmptyMessage(0);
                else
                    messageHandler.sendEmptyMessage(1);
            }
        }.start();



    }


    ///////// get imageBitmap
    private Bitmap downloadBitmap(String url) {

        Log.i("MyTag" , "Downloading Image now ...  ");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
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
                bitmap = photo.getScaledPhoto(mGoogleApiClient, 1000, 1000).await()
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
        Toast.makeText(getBaseContext(), "Connecion Lost >> " + connectionResult.getErrorMessage() , Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    new TripServices().startTrip(trip);
                } else {
                    // Permission Denied
                    Toast.makeText(TripDetailsActivity.this, "Accessing GPS is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

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
                ///deleteTrip();
                Alert.showConfimDeleteDialog(TripDetailsActivity.this, trip);
                return true;
            case R.id.action_done:
                doneTrip();
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doneTrip() {
//        Toast.makeText(this, "done trip", Toast.LENGTH_SHORT).show();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + SharedPreferenceInfo.getUserId(getApplicationContext()) + "/trips");
        db.child(trip.getTripId()).child("tripStatus").setValue(DBConstants.STATUS_DONE);
        finish();
    }

    private void deleteTrip() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users/" + SharedPreferenceInfo.getUserId(getApplicationContext()) + "/trips");
        db.child(trip.getTripId()).removeValue();
        finish();
    }
    /*==== END MENU ===*/
}
