package com.jets.mashaweer;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.nfc.tech.NfcBarcode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.classes.Alarm;
import com.jets.classes.Trip;
import com.jets.constants.SharedPreferenceInfo;

import java.util.Calendar;
import java.util.UUID;

public class TripAddActivity extends AppCompatActivity {

    private Button addTrip, tripDate, tripTime;
    private TextInputLayout tripName;
    private Trip tripObj;
    private int hours, minutes, year, month, day;
    private DB_Adapter db_adapter;
    private String userID;
    DatabaseReference db;
    ////// hint:: Mohem Gdn Gdn Gdnzzzz
    /////////////// for DB and TripBean
    //////////////////// StartLongitude will hold Long&Lat for start location "Semicolon Separated"
    //////////////////// StartLatitude  will hold PlaceName for start location
    //////////////////// EndLongitude will hold Long&Lat for destination location "Semicolon Separated"
    //////////////////// EndLatitude  will hold PlaceName for destination location
    //////////////////// TripDateTime will hold "22/10/2019 10:45" [separated by space]


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_add_ramadan);

        userID = SharedPreferenceInfo.getUserId(getApplicationContext());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = database.getReference("users/" + userID + "/trips");

        addTrip = (Button) findViewById(R.id.addTrip_addBtn);
        tripDate = (Button) findViewById(R.id.addTrip_tripDate);
        tripTime = (Button) findViewById(R.id.addTrip_tripTime);
        tripName = (TextInputLayout) findViewById(R.id.addTrip_tripName);

        tripObj = new Trip();
        db_adapter = new DB_Adapter(getApplicationContext());

        ////////////////// 1. get trip name in the ON ADD TRIP CLICK



        ////////////////// 2. get start location [Long&Lat + place name]
        PlaceAutocompleteFragment autocompleteFragment_FROM = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment_FROM.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("MyTag", "Place: " + place.getName());
                Log.i("MyTag", "PlaceID: " + place.getId());
                Log.i("MyTag", "PlaceAddress: " + place.getAddress());
                Log.i("MyTag", "Place: " + place.getLatLng().toString());


                String long_lat =  place.getLatLng().longitude +"";
                long_lat += ";" + place.getLatLng().latitude;

                tripObj.setTripStartLong( long_lat );   /////////////////////////////// 5ally balk
                tripObj.setTripStartLat( place.getName().toString() ); //////////////// 5ally balk
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MyTag", "An error occurred: " + status);
            }
        });


        ////////////////// 3. get destination location [Long&Lat + place name]
        final PlaceAutocompleteFragment autocompleteFragment_TO = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);

        autocompleteFragment_TO.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("MyTag", "Place: " + place.getName());
                Log.i("MyTag", "PlaceID: " + place.getId());
                Log.i("MyTag", "PlaceAddress: " + place.getAddress());
                Log.i("MyTag", "Place: " + place.getLatLng().toString());

                String long_lat =  place.getLatLng().longitude +"";
                long_lat += ";" + place.getLatLng().latitude;

                tripObj.setTripEndLong( long_lat );   /////////////////////////////// 5ally balk
                tripObj.setTripEndLAt( place.getName().toString() ); //////////////// 5ally balk


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MyTag", "An error occurred: " + status);
            }
        });


        /////////////////// 4. get trip date

        tripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TripAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                tripObj.setTripDateTime(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });


        //////////////////// 5. get trip time

        tripTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                hours = calendar.get(Calendar.HOUR);
                minutes = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(TripAddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String date = tripObj.getTripDateTime();

                                tripObj.setTripDateTime( date + " " + hourOfDay + ":" + minute);

                            }
                        }, hours, minutes, false);

                timePickerDialog.show();
            }
        });





        //////////////////////// Finally, Insert Into Db then go to another view
        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tripObj.setTripTitle( tripName.getEditText().getText().toString() );
                tripObj.setTripId(UUID.randomUUID().toString());

                db.child(tripObj.getTripId()).setValue(tripObj);

                Log.i("3lama","added to database");

                Intent intent = new Intent(TripAddActivity.this, HomeActivity.class);
                startActivity(intent);

                // Adding Alarm
                Intent alarmIntent = new Intent(TripAddActivity.this, Alarm.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        TripAddActivity.this.getApplicationContext(), 234324243, alarmIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                        + (10*1000), pendingIntent);
                Toast.makeText(TripAddActivity.this, "Alarm will fire in 3 seconds",Toast.LENGTH_LONG).show();

                finish();
//
//
//
//                if( db_adapter.insertTripInfo(tripObj))
//                {
//                    Log.i("MyTag", "Insertion process >>>>>>> SUCCESS " );

//                }else{
//
//                    Log.i("MyTag", "Insertion process >>>>>>> FAILED " );
//                }

            }
        });




    }
}
