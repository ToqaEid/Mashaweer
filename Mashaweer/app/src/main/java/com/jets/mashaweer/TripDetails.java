package com.jets.mashaweer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jets.classes.Trip;

public class TripDetails extends AppCompatActivity {

    Intent previousIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details2);

//        previousIntent = getIntent();
//        Trip trip = previousIntent.getParcelableExtra("trip");
//        trip.getName();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cairo");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.edit_floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripDetails.this, TripEditActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab_play = (FloatingActionButton) findViewById(R.id.play_floating_button);
        fab_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripDetails.this, HistoryActivity.class);
                startActivity(intent);
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
