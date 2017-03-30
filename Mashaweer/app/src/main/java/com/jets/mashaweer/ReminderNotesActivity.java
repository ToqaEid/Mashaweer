package com.jets.mashaweer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
        this.setFinishOnTouchOutside(false);
        ButterKnife.bind(this);

        Window window = getWindow();
        if(!window.isActive()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        }

        intent = getIntent();
        trip = (Trip) intent.getSerializableExtra("trip");
        notesPreparation();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /*===================================== HELPFUL FUNCTIONS ========================================*/
    private void notesPreparation(){



        //////// NOTES

        uncheckedNotes = trip.getTripUncheckedNotes();
        checkedNotes = trip.getTripCheckedNotes();

        if(uncheckedNotes == null ){
            uncheckedNotes = new ArrayList<>();
        }
        if(checkedNotes == null){
            checkedNotes = new ArrayList<>();
        }

        if(uncheckedNotes.size() == 0 && checkedNotes.size() == 0){
            uncompeletedText.setText("No Notes For This Trip");
            Log.i("notes", "before");
            return;

        }
        Log.i("notes", "after");
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


    }


}
