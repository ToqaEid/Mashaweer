package com.jets.mashaweer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jets.adapters.TabsAdapter;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.constants.DBConstants;
import com.jets.constants.SharedPreferenceInfo;
import com.jets.fragments.NavBarFragment;
import com.jets.fragments.PastTripsFragment;
import com.jets.fragments.UpcomingTripsFragment;
import com.jets.interfaces.Communicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ActionBar.TabListener , Communicator , GoogleApiClient.OnConnectionFailedListener {

    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private ActionBar actionBar;
    public static String userID ;
    public UpcomingTripsFragment upcomingTripsFragment;
    public PastTripsFragment pastTripsFragment;

    private ArrayList<Trip> pastTrips;
    private ArrayList<Trip> upcomingTrips;
    private ArrayList<Trip> roundTrips;


    public static int imagesCount =0, loadedCount =0;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i("Test","onCreate_ HomeActivity");

        setTitle("Mashaweer");

        setContentView(R.layout.activity_main_home);

        NavBarFragment fragment = (NavBarFragment) getSupportFragmentManager().findFragmentById(R.id.navbar);
        fragment.setBtnColor("home");

        prepareViewPager();


    }

    @Override
    public void onResume() {
        super.onResume();


        Log.i("Test","onResume_ HomeActivity");

        Log.i("trip", "onResume here");

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        // reading and updating data from database
        userID = SharedPreferenceInfo.getUserId(this);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference db = database.getReference("users/" + userID);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("trip", "onResume onDataChange");

                upcomingTrips.clear();
                roundTrips.clear();
                pastTrips.clear();

                Iterable<DataSnapshot> trips = dataSnapshot.child("trips").getChildren();


                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SharedPreferenceInfo.PREFS_NAME, MODE_PRIVATE);
                boolean alarmflag = sharedPreferences.getBoolean(SharedPreferenceInfo.ALARMS_SET, false);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                while (trips.iterator().hasNext()) {

                    DataSnapshot returnedData = trips.iterator().next();
                    final Trip trip = returnedData.getValue(Trip.class);
                    trip.setTripId(returnedData.getKey());

                    //changing notes to empty array if null
                    if (trip.getTripCheckedNotes() == null) {
                        trip.setTripCheckedNotes(new ArrayList<String>());
                    }

                    if (trip.getTripUncheckedNotes() == null) {
                        trip.setTripUncheckedNotes(new ArrayList<String>());
                    }


                    switch (trip.getTripStatus()) {

                        case DBConstants.STATUS_UPCOMING:
                            upcomingTrips.add(trip);
                            Log.i("3lama", "Alaram Flag " + alarmflag);
                            if (alarmflag) {


//                                //TODO Check if image exists in internal storage
//                                // TODO if NOT, Then downLoad it
//
//                                Log.i("MyTag", "Getting Images internal");
//
//                                 Bitmap tripImage = loadImageFromStorage( getFilesDir().getAbsolutePath() , trip.getTripPlaceId());
//
//                                Log.i("MyTag","Back from internal storage");
//
//                                if (tripImage == null)
//                                {
//                                    new Thread() {
//                                        public void run() {
//
//                                            Log.i("MyTag" , "Thread is running ....");
//                                            Log.i("MyTag" , "Getting >> " + trip.getTripPlaceId());
//
//                                            Bitmap bitmap = downloadBitmap(  trip.getTripPlaceId() );
//
//
//                                            if (bitmap == null){
//
//                                                Log.i("MyTag" ,"Image Not Found ");
//
//                                            }
//                                            else {
//
//                                                ///////////// ---- saving photo to internal storage
//                                                String path = saveToInternalStorage(bitmap, trip.getTripPlaceId());
//                                                Log.i("MyTag" ,"Image is Saved in " + path);
//
//                                            }
//
//                                            loadedCount++;
//
////                                            if (loadedCount == imagesCount)
////                                                progressDialog.dismiss();
//
//
//                                        }
//                                    }.start();
//
//                                }



                                if (trip.getTripDateTime() > System.currentTimeMillis())
                                    TripServices.setAlarm(HomeActivity.this, trip);

                            }
                            break;

                        case DBConstants.STATUS_PENDING:
                            roundTrips.add(trip);
                            Log.i("3lama + round trips", " " + trip.toString());

                            break;

                        default:
                            pastTrips.add(trip);
                            break;
                    }
                }
                Log.i("home", "Home "+pastTrips.size());

                pastTripsFragment.refreshData(pastTrips);
                upcomingTripsFragment.refreshData(upcomingTrips, roundTrips);
                editor.putBoolean(SharedPreferenceInfo.ALARMS_SET, false);
                editor.commit();
                alarmflag = sharedPreferences.getBoolean(SharedPreferenceInfo.ALARMS_SET, false);

                Log.i("3lama", "changing flag to false Flag " + alarmflag );
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
            }
        });

    }

    //////////////////////// tabs ya basha

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.i("Test","onTabSelected HomeActivity");
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.i("Test","onTabUnselected HomeActivity");
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.i("Test","onTabReselected HomeActivity");
    }

    @Override
    public void sendMsg(Trip trip) {

        Intent intent = new Intent(getApplicationContext(),TripDetailsActivity.class);
        intent.putExtra("selectedTrip", trip);
        startActivity(intent);

    }

    private void prepareViewPager(){
        viewPager = (ViewPager) findViewById(R.id.pager);
        //getting userID from intent
        userID = getIntent().getStringExtra("userID");

        actionBar = getSupportActionBar();
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        upcomingTrips = new ArrayList<>();
        roundTrips = new ArrayList<>();
        pastTrips = new ArrayList<>();

        pastTripsFragment = tabsAdapter.getPastTripsFragment();
        upcomingTripsFragment = tabsAdapter.getUpcomingTripsFragment();

        viewPager.setAdapter(tabsAdapter);

        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        ///////// add your tabs
        for(int i=0; i<TabsAdapter.tabNames.length; i++)
        {
            ActionBar.Tab tab = actionBar.newTab().setText( TabsAdapter.tabNames[i].toString() ).setTabListener(this);
            actionBar.addTab(tab);
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    /////////// get PastTripsArrayList to History

    public ArrayList<Trip> getPastTripsArrayList (){

        return pastTrips;
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


    ////////////////////// ---- download image
    private Bitmap downloadBitmap(String url) {

        Bitmap bitmap = null;

        Log.i("MyTag" , "Downloading Image now ...  ");

        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(HomeActivity.this)
                    // .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Places.GEO_DATA_API)
                    .build();

            mGoogleApiClient.connect();
        }
        else if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, url).await();

        if (result.getStatus().isSuccess()) {

            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();

            if (photoMetadataBuffer.getCount() > 0) {

                int rand = (int) (Math.floor(Math.random()) * photoMetadataBuffer.getCount());

                PlacePhotoMetadata photo = photoMetadataBuffer.get(rand);

                bitmap = photo.getScaledPhoto(mGoogleApiClient, 300, 300).await().getBitmap();

            }
            else{
                bitmap = null;
            }

            photoMetadataBuffer.release();
        }

        return bitmap;
    }



    ///////////// save image to internal storage
    private String saveToInternalStorage(Bitmap bitmapImage, String targetPlaceId){

        Log.i("MyTag", "Saving into internal");

        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath=new File(directory, targetPlaceId + ".jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mypath);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

            Log.i("MyTag", "Saved into internal");

        } catch (Exception e) {

            e.printStackTrace();

        }
        finally {

            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.i("MyTag", "image to be returned");

        return directory.getAbsolutePath();
    }




    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(HomeActivity.this, "Connecion Lost >> " + connectionResult.getErrorMessage() , Toast.LENGTH_SHORT).show();

    }


    public ArrayList<Trip> getPastTrips(){
        return pastTrips;
    }
}
