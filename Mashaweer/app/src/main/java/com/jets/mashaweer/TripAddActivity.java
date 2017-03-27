package com.jets.mashaweer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.adapters.notes.checkednotes.CheckedNoteAdatper;
import com.jets.adapters.notes.uncheckednotes.NotesAdapter;
import com.jets.classes.ListFormat;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;
import com.jets.constants.Alert;
import com.jets.constants.DBConstants;
import com.jets.constants.SharedPreferenceInfo;

import java.util.ArrayList;
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
    @BindView(R.id.trip_type) Switch _tripType;
    @BindView(R.id.checked_list) ListView checkedList;
    @BindView(R.id.unchecked_list) ListView uncheckedList;
    @BindView(R.id.note_input) EditText noteInput;
    @BindView(R.id.cancel_note) ImageView cancelBtn;
    @BindView(R.id.flag) TextView noteFlag;

    //Arrays of year Months
    String[] monthsOfYear = {"JAN", "FEB", "MAR", "April", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};


    //Other variables
    private Trip tripObj;
    private ArrayList<String> uncheckedNotes;
    private ArrayList<String> checkedNotes;
    private NotesAdapter uncheckedAdapter;
    private CheckedNoteAdatper checkedAdapter;
    private int hours, minutes, year, month, day;
    private String userID;
    private DatabaseReference db;
    private Calendar calender = Calendar.getInstance();
    private String activityFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_trip);

        ButterKnife.bind(this);

        userID = SharedPreferenceInfo.getUserId(getApplicationContext());

        tripObj = (Trip) getIntent().getSerializableExtra("selectedTrip");


        if (tripObj != null){   //edit activity

            //filling fields with data if Editing
            fillingEditData();
            activityFlag ="edit";
//            uncheckedNotes = tripObj.getTripUncheckedNotes();
//            checkedNotes = tripObj.getTripCheckedNotes();
            uncheckedNotes = new ArrayList<>();
            checkedNotes =  new ArrayList<>();

        }else {
            tripObj = new Trip();
            activityFlag ="add";
            uncheckedNotes = new ArrayList<>();

        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        db = database.getReference("users/" + userID + "/trips");
        notesPreparation();

//        db_adapter = new DB_Adapter(getApplicationContext());
//        calender = Calendar.getInstance();


        ////////////////// 2. get start & end location [Long&Lat + place name]
        prepareTripLocations();

        /////////////////// 3. get trip date & time
        prepareDateAndTime();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        new MenuInflater(getApplication()).inflate(R.menu.menu_add_edit_trip, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveTrip();
                break;

            default:
                break;
        }

        return true;
    }

    /*=========================== HELPFUL FUNCTIONS =========================*/

    private void fillingEditData(){
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

    }

    private void prepareTripLocations (){
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

                tripObj.setTripEndLongLat( long_lat );   /////////////////////////////// 5ally balk
                tripObj.setTripEndLocation( place.getName().toString() ); //////////////// 5ally balk


                tripObj.setTripPlaceId( place.getId() );

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MyTag", "An error occurred: " + status);
            }
        });

    }

    private void prepareDateAndTime(){
        _datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(TripAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                calender.set(Calendar.MONTH, monthOfYear);
                                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                calender.set(Calendar.YEAR, year);

                                Log.i("3lama", calender.getTimeInMillis()+" ---- DATEpicker time in milliseconds");

                                _day.setText(String.valueOf(dayOfMonth));
                                _month.setText(monthsOfYear[monthOfYear]);
                                _year.setText(String.valueOf(year));
                            }
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
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
                                calender.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                calender.set(Calendar.MINUTE,minute);
                                calender.set(Calendar.SECOND,0);

                                Log.i("3lama", "Hour of day: "+hourOfDay  + " calendar date " + calender.getTimeInMillis());

                                Log.i("3lama", calender.getTimeInMillis()+" ---- TIMEpicker time in milliseconds");

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

                timePickerDialog.show();
            }
        });

    }

    private void saveTrip(){
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
        //setting uncheckedNotes into object
        tripObj.setTripUncheckedNotes(uncheckedNotes);

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
        Log.i("3lama", "Trip object before adding to database --- " + tripObj.toString());
        db.child(tripObj.getTripId()).setValue(tripObj);

        // Adding Alarm
        TripServices.setAlarm(TripAddActivity.this, tripObj);

        Intent intent = new Intent();
        intent.putExtra("newTrip", tripObj);
        setResult(Activity.RESULT_OK, intent);

        finish();

    }

    public void notesPreparation(){
        Log.i("tag notes prep", "hey");

        if(activityFlag.equals("add")){
            checkedList.setVisibility(View.GONE);
        }else{  //activity is edit


            /*TESTING BLOCK*/
            checkedNotes.add("One");
            checkedNotes.add("two");

            checkedAdapter = new CheckedNoteAdatper(TripAddActivity.this, checkedNotes);
            checkedAdapter.setActivityFlag("edit");
            checkedList.setAdapter(checkedAdapter);
            ListFormat.setListViewHeightBasedOnChildren(checkedList);

            checkedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    noteInput.setText(checkedNotes.get(position));
                    noteFlag.setText("checked");
                    checkedNotes.remove(position);
                    checkedAdapter.notifyDataSetChanged();
                    ListFormat.setListViewHeightBasedOnChildren(checkedList);
                }
            });
        }

        uncheckedAdapter = new NotesAdapter(TripAddActivity.this, uncheckedNotes);
        uncheckedAdapter.setActivityFlag("add");
        uncheckedList.setAdapter(uncheckedAdapter);

        uncheckedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                noteInput.setText(uncheckedNotes.get(position));
                noteFlag.setText("unchecked");
                uncheckedNotes.remove(position);
                uncheckedAdapter.notifyDataSetChanged();
                ListFormat.setListViewHeightBasedOnChildren(uncheckedList);
            }
        });


        //handle enter action
        noteInput.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            //add to array list & update list view
                            if(noteFlag.getText().toString().equals("checked")){
                                checkedNotes.add(noteInput.getText().toString());
                                checkedAdapter.notifyDataSetChanged();
                                ListFormat.setListViewHeightBasedOnChildren(checkedList);
                            }else{
                                uncheckedNotes.add(noteInput.getText().toString());
                                uncheckedAdapter.notifyDataSetChanged();
                                ListFormat.setListViewHeightBasedOnChildren(uncheckedList);
                            }

                            //clear text field
                            noteInput.setText("");
                            noteFlag.setText("");
                            //hide keyboard
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(noteInput.getWindowToken(), 0);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tag notes btn", noteInput.getText().toString());

                noteInput.setText("");
            }
        });


    }


    /*===================== Public Instance Functions ====================*/
    public void removeFromUncheckedList(int item){
        if (activityFlag.equals("add")) {
            uncheckedNotes.remove(item);
            uncheckedAdapter.notifyDataSetChanged();
            ListFormat.setListViewHeightBasedOnChildren(uncheckedList);
        }else {
            checkedNotes.remove(item);
            checkedAdapter.notifyDataSetChanged();
            ListFormat.setListViewHeightBasedOnChildren(checkedList);
        }
    }
}
