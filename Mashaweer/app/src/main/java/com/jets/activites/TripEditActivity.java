package com.jets.activites;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.classes.Alarm;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.constants.SharedPreferenceInfo;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;


public class TripEditActivity extends AppCompatActivity {

    private Button addTrip, tripDate, tripTime;
    private ToggleButton tripType;
    private TextInputLayout tripName;
    private int hours, minutes, year, month, day;
    private String userID;
    DatabaseReference db;
    Intent previousIntent;
    private Trip trip;

    String timeStr, dateStr ;

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
        tripType = (ToggleButton) findViewById(R.id.addTrip_tripType) ;

        previousIntent = getIntent();
        trip = (Trip) previousIntent.getSerializableExtra("selectedTrip");

        //fill fields with data
        tripName.getEditText().setText(trip.getTripTitle());
        final String[] dateTime = trip.getTripDateTime().split(" ");

        tripDate.setText(dateTime[0]);
        tripTime.setText(dateTime[1]);

        Toast.makeText(TripEditActivity.this, String.valueOf(trip.getTripType()), Toast.LENGTH_SHORT).show();
        if(trip.getTripType() != 0){
            tripType.toggle();
        }


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

        ////////////////// 2. get start location [Long&Lat + place name]
        PlaceAutocompleteFragment autocompleteFragment_FROM = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment_FROM.setHint(trip.getTripStartLat());

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

                trip.setTripStartLong( long_lat );   /////////////////////////////// 5ally balk
                trip.setTripStartLat( place.getName().toString() ); //////////////// 5ally balk
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MyTag", "An error occurred: " + status);
            }
        });


        ////////////////// 3. get destination location [Long&Lat + place name]
        PlaceAutocompleteFragment autocompleteFragment_TO = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);

        autocompleteFragment_TO.setHint(trip.getTripEndLAt());
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

                trip.setTripEndLong( long_lat );   /////////////////////////////// 5ally balk
                trip.setTripEndLAt( place.getName().toString() ); //////////////// 5ally balk


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MyTag", "An error occurred: " + status);
            }
        });


        tripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TripEditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dateStr = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                                tripDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

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

                TimePickerDialog timePickerDialog = new TimePickerDialog(TripEditActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String date = trip.getTripDateTime();

                                tripTime.setText(hourOfDay + ":" + minute);
                                timeStr = hourOfDay + ":" + minute;

                            }
                        }, hours, minutes, false);

                timePickerDialog.show();
            }
        });





        //////////////////////// Finally, Insert Into Db then go to another view
        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dateStr != null && timeStr != null){
                    trip.setTripDateTime( dateStr + " " + timeStr);
                }

                trip.setTripTitle( tripName.getEditText().getText().toString() );

                db.child(trip.getTripId()).setValue(trip);

                Log.i("3lama","added to database");

//                Intent intent = new Intent(TripEditActivity.this, HomeActivity.class);
//                startActivity(intent);

                // Adding Alarm
                TripServices.setAlarm(TripEditActivity.this, trip, System.currentTimeMillis() + (10*1000));


                Toast.makeText(TripEditActivity.this, "Alarm will fire in 3 seconds",Toast.LENGTH_LONG).show();

                finish();
//
//
//
//                if( db_adapter.insertTripInfo(trip))
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
