package com.jets.mashaweer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.constants.Alert;
import com.jets.constants.DBConstants;
import com.jets.constants.SharedPreferenceInfo;

import java.util.Calendar;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TripAddActivity extends AppCompatActivity {

    //Binding vies to variables
    @BindView(R.id.trip_name) EditText _tripName;
    @BindView(R.id.day) TextView _day;
    @BindView(R.id.month) TextView _month;
    @BindView(R.id.year) TextView _year;
    @BindView(R.id.hours) TextView _hour;
    @BindView(R.id.minutes) TextView _minutes;
    @BindView(R.id.ampm) TextView _ampm;
    @BindView(R.id.datepick) LinearLayout _datePick;
    @BindView(R.id.timepick) LinearLayout _timePick;
    @BindView(R.id.save_trip) Button _saveTrip;
    @BindView(R.id.trip_type) Switch _tripType;

    //Arrays of year Months
    String[] monthsOfYear = {"JAN", "FEB", "MAR", "April", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};


    //Other variables
    private Trip tripObj;
    private int hours, minutes, year, month, day;
    private String userID;
    DatabaseReference db;
    private Calendar calender = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_trip);

        ButterKnife.bind(this);

        userID = SharedPreferenceInfo.getUserId(getApplicationContext());

        tripObj = (Trip) getIntent().getSerializableExtra("selectedTrip");

        if (tripObj != null){

            //filling fields with data if Editing
            calender.setTimeInMillis(tripObj.getTripDateTime());

            int mYear = calender.get(Calendar.YEAR);
            int mMonth = calender.get(Calendar.MONTH);
            int mDay = calender.get(Calendar.DAY_OF_MONTH);
            int mHour = calender.get(Calendar.HOUR);
            int mMinute = calender.get(Calendar.MINUTE);

            //Setting date
            _day.setText(String.valueOf(mDay));
            _month.setText(monthsOfYear[mMonth]);
            _year.setText(String.valueOf(mYear));

            //Setting time
            if ( mHour == 0){
                _hour.setText(String.valueOf(12));
                _ampm.setText("A.M.");
            }else if (mHour > 12){
                _hour.setText(String.valueOf(mHour-12));
                _ampm.setText("P.M.");
            }else{
                _hour.setText(String.valueOf(mHour));
                _ampm.setText("A.M.");
            }
            String mnts;
            if (mMinute <10){
                mnts = ": 0" + mMinute;
            }else{
                mnts = ": " + mMinute;
            }
            _minutes.setText(mnts);

            //Setting trip name
            _tripName.setText(tripObj.getTripTitle());

            //Setting trip type
            if (tripObj.getTripType() == DBConstants.TYPE_ROUND_TRIP){
                _tripType.setChecked(true);
            }


        }else {
            tripObj = new Trip();
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = database.getReference("users/" + userID + "/trips");


//        db_adapter = new DB_Adapter(getApplicationContext());
//        calender = Calendar.getInstance();


        ////////////////// 2. get start location [Long&Lat + place name]
        PlaceAutocompleteFragment autocompleteFragment_FROM = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        if (tripObj.getTripStartLongLat() != null){
            autocompleteFragment_FROM.setText(tripObj.getTripStartLocation());
        }
        autocompleteFragment_FROM.setHint("Pick a Start-Point");

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

                tripObj.setTripStartLongLat( long_lat );   /////////////////////////////// 5ally balk
                tripObj.setTripStartLocation( place.getName().toString() ); //////////////// 5ally balk
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

        if (tripObj.getTripEndLongLat() != null){
            autocompleteFragment_TO.setText(tripObj.getTripEndLocation());
        }
        autocompleteFragment_TO.setHint("Pick a destination");

        autocompleteFragment_TO.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                String long_lat =  place.getLatLng().longitude +"";
                long_lat += ";" + place.getLatLng().latitude;

                tripObj.setTripEndLongLat( long_lat );
                tripObj.setTripEndLocation( place.getName().toString() );
                tripObj.setTripPlaceId( place.getId() );
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MyTag", "An error occurred: " + status);
            }
        });


        /////////////////// 4. get trip date

        _datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final Calendar calendar = Calendar.getInstance();
//                year = calendar.get(Calendar.YEAR);
//                month = calendar.get(Calendar.MONTH);
//                day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(TripAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                calender.set(Calendar.YEAR, year);
                                calender.set(Calendar.MONTH, monthOfYear);
                                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                _day.setText(String.valueOf(dayOfMonth));
                                _month.setText(monthsOfYear[monthOfYear]);
                                _year.setText(String.valueOf(year));
                            }
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        //////////////////// 5. get trip time

        _timePick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

//                final Calendar calendar = Calendar.getInstance();
//                hours = calendar.get(Calendar.HOUR);
//                minutes = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(TripAddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                               // String date = tripObj.getTripDateTime();
                                calender.set(Calendar.HOUR,hourOfDay);
                                calender.set(Calendar.MINUTE,minute);
                                calender.set(Calendar.SECOND,0);

                                Log.i("3lama", "On Time Picker: ---- " + calender.getTimeInMillis());

                                if ( hourOfDay == 0){
                                    _hour.setText(String.valueOf(12));
                                    _ampm.setText("A.M.");
                                }else if (hourOfDay > 12){
                                    _hour.setText(String.valueOf(hourOfDay-12));
                                    _ampm.setText("P.M.");
                                }else{
                                    _hour.setText(String.valueOf(hourOfDay));
                                    _ampm.setText("A.M.");
                                }
                                String mnts;
                                Log.i("3lama", String.valueOf(minute));
                                if (minute <10){
                                    mnts = ": 0" + minute;
                                }else{
                                    mnts = ": " + minute;
                                }

                                _minutes.setText(mnts);

                            }
                        }, hours, minutes, false);

                //TODO: find a way that if the user picked today ... limit the time to after the current time
                timePickerDialog.show();
            }
        });



        //////////////////////// Finally, Insert Into Db then go to another view
        _saveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Setting trip name and ID
                if (tripObj.getTripId() == null){
                    tripObj.setTripId(UUID.randomUUID().toString());
                }

                tripObj.setTripTitle( _tripName.getText().toString());

                //Setting trip type & status
                if (_tripType.isChecked()){
                    tripObj.setTripType(DBConstants.TYPE_ROUND_TRIP);
                }else{
                    tripObj.setTripType(DBConstants.TYPE_ONE_WAY);
                }
                tripObj.setTripStatus(DBConstants.STATUS_UPCOMING);

                //setting trip time
                long selectedDateTime = calender.getTimeInMillis();
                tripObj.setTripDateTime(selectedDateTime);

                Log.i("3lama", selectedDateTime+" -------");

                Log.i("3lama", tripObj.toString());

                //Validation of data
                if (tripObj.getTripTitle().trim().isEmpty()){
                    Alert.showErrorMsg("Trip Name", "Please enter a trip name", TripAddActivity.this);
                    Log.i("3lama", "mafesh title");
                    return;
                }
                if (tripObj.getTripEndLongLat() == null){
                    Alert.showErrorMsg("No Destination", "Please pick a Destination", TripAddActivity.this);
                    Log.i("3lama", "mafesh End");
                    return;
                }
                if (tripObj.getTripStartLongLat() == null){
                    Alert.showErrorMsg("No Start-point", "Please pick a Start-Point", TripAddActivity.this);
                    Log.i("3lama", "mafesh From");
                    return;
                }

                if (tripObj.getTripStartLongLat().equals(tripObj.getTripEndLongLat())){
                    Alert.showErrorMsg("Same place", "You picked the same place as both destination and Start\n\nPlease pick a different location", TripAddActivity.this);
                    Log.i("3lama", "same place");
                    return;
                }

                Log.i("3lama", tripObj.getTripDateTime() + "");
                Log.i("3lama", System.currentTimeMillis() + "");
                if (tripObj.getTripDateTime() < System.currentTimeMillis()){
                    Alert.showErrorMsg("Date / Time", "Please pick a valid date and time", TripAddActivity.this);
                    Log.i("3lama", "mafesh Time");
                    return;
                }

                //Adding trip object to database
                db.child(tripObj.getTripId()).setValue(tripObj);

                // Adding Alarm
                TripServices.setAlarm(TripAddActivity.this, tripObj, tripObj.getTripDateTime());

                finish();

            }
        });


    }
}
