package com.jets.mashaweer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jets.adapters.notes.checkednotes.CheckedNoteAdatper;
import com.jets.adapters.notes.uncheckednotes.NotesAdapter;
import com.jets.classes.ListFormat;
import com.jets.classes.Trip;
import com.jets.classes.TripServices;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by toqae on 3/29/2017.
 */

public class ReminderNotesActivity extends Activity {
    @BindView(R.id.reminder_uncomplete_text)
    TextView uncompeletedText;
    @BindView(R.id.reminder_complete_text)
    TextView completeText;
    @BindView(R.id.reminder_uncompleted_list)
    ListView uncheckedList;
    @BindView(R.id.reminder_completed_list)
    ListView checkedList;
    @BindView(R.id.reminder_complete_line)
    View completeView;



    private Intent intent;
    private Trip trip;
    private ArrayList<String> checkedNotes;
    private ArrayList<String> uncheckedNotes;
    private CheckedNoteAdatper checkedNotesAdapter;
    private NotesAdapter uncheckedNotesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_notes);
        ButterKnife.bind(this);

        intent = getIntent();
        trip = (Trip) intent.getSerializableExtra("trip");
        notesPreparation();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        trip.setTripCheckedNotes(checkedNotes);
        trip.setTripUncheckedNotes(uncheckedNotes);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference db = database.getReference("users/" + TripServices.getTripUniqueId(trip.getTripId()) + "/trips");
        db.child(trip.getTripId()).setValue(trip);
    }

    /*===================================== HELPFUL FUNCTIONS ========================================*/
    private void notesPreparation(){



        //////// NOTES

        uncheckedNotes = trip.getTripUncheckedNotes();
        checkedNotes = trip.getTripCheckedNotes();

        if(uncheckedNotes == null){
            uncheckedNotes = new ArrayList<>();
        }
        if(checkedNotes == null){
            checkedNotes = new ArrayList<>();
        }

        checkedNotesAdapter = new CheckedNoteAdatper(this, checkedNotes);
        checkedNotesAdapter.setActivityFlag("details");
        checkedList.setAdapter(checkedNotesAdapter);

        uncheckedNotesAdapter = new NotesAdapter(this, uncheckedNotes);
        uncheckedNotesAdapter.setActivityFlag("details");
        uncheckedList.setAdapter(uncheckedNotesAdapter);

        if(checkedNotes.size() > 0){    //there's checked notes
            completeText.setVisibility(View.VISIBLE);
            completeView.setVisibility(View.VISIBLE);
        }
        if(uncheckedNotes.size() == 0){
            uncompeletedText.setVisibility(View.GONE);
            uncompeletedText.setVisibility(View.GONE);
        }

        checkedNotesAdapter.notifyDataSetChanged();
        ListFormat.setListViewHeightBasedOnChildren(checkedList);
        uncheckedNotesAdapter.notifyDataSetChanged();
        ListFormat.setListViewHeightBasedOnChildren(uncheckedList);


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

//        uncheckedList.setAdapter(uncheckedNotesAdapter);
//        checkedList.setAdapter(checkedNotesAdapter);
//
//        ListFormat.setListViewHeightBasedOnChildren(uncheckedList);
//        ListFormat.setListViewHeightBasedOnChildren(checkedList);

    }


}
