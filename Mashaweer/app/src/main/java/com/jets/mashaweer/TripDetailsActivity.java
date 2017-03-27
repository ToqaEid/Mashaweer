package com.jets.mashaweer;

import android.app.ActionBar;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.jets.adapters.notes.checkednotes.CheckedNoteAdatper;
import com.jets.adapters.notes.checkednotes.CheckedNoteViewHolder;
import com.jets.adapters.notes.uncheckednotes.NotesAdapter;
import com.jets.classes.ListFormat;
import com.jets.classes.Note;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.constants.Alert;
import com.jets.constants.DBConstants;
import com.jets.constants.SharedPreferenceInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TripDetailsActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener {

    //UI References
    private TextView tv_tripStatus, tv_tripFrom, tv_tripTo, tv_tripDate, tv_tripTime_1, tv_tripTime_2, completeText, uncompeletedText;
    private View completeView;
    private ListView uncheckedList, checkedList;
    private GoogleApiClient mGoogleApiClient;
    private Bitmap bitmap = null;
    private ImageView imageViewTrip;

    //Location References
    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private double longitude = 0, latitude = 0;

    //General Referances
    private Intent previousIntent;
    private Trip trip;
    private NotesAdapter uncheckedNotesAdapter;
    private CheckedNoteAdatper checkedNotesAdapter;
    private ArrayList<String> uncheckedNotes;
    private ArrayList<String> checkedNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details2);

        if (savedInstanceState == null) {
            previousIntent = getIntent();
            trip = (Trip) previousIntent.getSerializableExtra("selectedTrip");
        } else {
            trip = (Trip) savedInstanceState.getSerializable("trip");
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(trip.getTripTitle());
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        //////////////////  egt references for TextViews

        tv_tripStatus = (TextView) findViewById(R.id.tripDetails_tv_status);
        tv_tripDate = (TextView) findViewById(R.id.tripDetails_tv_date);
        tv_tripFrom = (TextView) findViewById(R.id.tripDetails_tv_from);
        tv_tripTo = (TextView) findViewById(R.id.tripDetails_tv_to);
        tv_tripTime_1 = (TextView) findViewById(R.id.tripDetails_tv_time1);
        tv_tripTime_2 = (TextView) findViewById(R.id.tripDetails_tv_time2);
        completeText = (TextView) findViewById(R.id.complete_text);
        uncompeletedText = (TextView) findViewById(R.id.uncomplete_text);
        completeView = findViewById(R.id.complete_line);
        imageViewTrip = (ImageView) findViewById(R.id.tripDetails_Image);

        /////////////// populate data in tripVeiw

        if (trip.getTripStatus() == 1)
            tv_tripStatus.setText("UpComing Trip");
        else
            tv_tripStatus.setText("Done Trip");

        tv_tripFrom.setText(trip.getTripStartLocation());
        tv_tripTo.setText(trip.getTripEndLocation());


        ///Notes
        notesPreparation();

        /////// get date and time from milliseconds
        prepareDateTime();

        //////////////// handling buttons' click listener
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.edit_floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TripDetailsActivity.this, TripAddActivity.class);
                intent.putExtra("selectedTrip", trip);
                startActivity(intent);

            }
        });

        FloatingActionButton fab_play = (FloatingActionButton) findViewById(R.id.play_floating_button);
        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gpsPermission();
                new TripServices().startTrip(trip);

            }
        });

        Log.i("invi", String.valueOf(trip.getTripStatus()));
        if (trip.getTripStatus() == DBConstants.STATUS_DONE) {
            Log.i("Tag", "invisible");
            fab.setVisibility(View.INVISIBLE);
            fab_play.setVisibility(View.GONE);

        }

        Bitmap img = loadImageFromStorage( getFilesDir().getAbsolutePath() , trip.getTripPlaceId());

        Log.i("MyTag","Back from internal storage");

        if (img != null)
          imageViewTrip.setImageBitmap( img );
        else {
            img = BitmapFactory.decodeResource(getResources(), R.drawable.trip2);   ///// default image
            imageViewTrip.setImageBitmap(img);
        }





    }



    @Override
    protected void onPause() {
        super.onPause();
        trip.setTripCheckedNotes(checkedNotes);
        trip.setTripUncheckedNotes(uncheckedNotes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO: save the object in db

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("trip", trip);
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
                Alert.showErrorMsg("sdfg","sdfg", TripDetailsActivity.this);
                doneTrip();
                return true;
//            case R.id.home:
//                finish();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doneTrip() {
//        Toast.makeText(this, "done trip", Toast.LENGTH_SHORT).show();



    }

    /*==== END MENU ===*/

    /*================ HELPFUL FUNCTIONS =================================*/
    private void notesPreparation(){



        //////// NOTES
        uncheckedNotes = new ArrayList<>();
        checkedNotes = new ArrayList<>();
        uncheckedNotes.add("One");
        uncheckedNotes.add("twO");
        uncheckedNotes.add("One");
        uncheckedNotes.add("One");

        uncheckedNotesAdapter = new NotesAdapter(this, uncheckedNotes);
        checkedNotesAdapter = new CheckedNoteAdatper(this, checkedNotes);

        uncheckedNotesAdapter.setActivityFlag("details");
        checkedNotesAdapter.setActivityFlag("details");

        uncheckedList = (ListView) findViewById(R.id.uncompleted_list);
        uncheckedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                checkedNotes.add(uncheckedNotes.get(position));
                uncheckedNotes.remove(position);
                uncheckedNotesAdapter.notifyDataSetChanged();
                checkedNotesAdapter.notifyDataSetChanged();
                ListFormat.setListViewHeightBasedOnChildren(uncheckedList);
                ListFormat.setListViewHeightBasedOnChildren(checkedList);
                if(checkedNotes.size() > 0){
                    completeText.setVisibility(View.VISIBLE);
                    completeView.setVisibility(View.VISIBLE);
                }
                if(uncheckedNotes.size() ==0){
                    uncompeletedText.setVisibility(View.GONE);
                    completeView.setVisibility(View.GONE);
                }
            }
        });

        checkedList =(ListView) findViewById(R.id.completed_list);
        checkedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                uncheckedNotes.add(checkedNotes.get(position));
                checkedNotes.remove(position);
                uncheckedNotesAdapter.notifyDataSetChanged();
                checkedNotesAdapter.notifyDataSetChanged();
                ListFormat.setListViewHeightBasedOnChildren(uncheckedList);
                ListFormat.setListViewHeightBasedOnChildren(checkedList);
                if(checkedNotes.size() == 0){
                    completeText.setVisibility(View.GONE);
                    completeView.setVisibility(View.GONE);
                }
                if(uncheckedNotes.size() >0){
                    uncompeletedText.setVisibility(View.VISIBLE);
                    completeView.setVisibility(View.VISIBLE);
                }

            }
        });

        uncheckedList.setAdapter(uncheckedNotesAdapter);
        checkedList.setAdapter(checkedNotesAdapter);

        ListFormat.setListViewHeightBasedOnChildren(uncheckedList);
        ListFormat.setListViewHeightBasedOnChildren(checkedList);

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





    private void prepareDateTime(){
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
    }

    private void gpsPermission(){
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
}
