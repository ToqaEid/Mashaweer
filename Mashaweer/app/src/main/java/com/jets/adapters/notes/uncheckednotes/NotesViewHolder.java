package com.jets.adapters.notes.uncheckednotes;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jets.mashaweer.R;

/**
 * Created by toqae on 3/24/2017.
 */

public class NotesViewHolder {

    View convertedView;
    CheckBox noteCheck;
    TextView noteItem;

    public NotesViewHolder(View view)
    {
        convertedView = view ;
    }

    public CheckBox getNoteCheck() {

        if(noteCheck == null)
            noteCheck = (CheckBox) convertedView.findViewById(R.id.note_check);

        return noteCheck;
    }

    public void setNoteCheck(CheckBox noteCheck) {
        this.noteCheck = noteCheck;
    }
    public TextView getNoteItem() {

        if(noteItem == null)
            noteItem = (TextView) convertedView.findViewById(R.id.note_text);

        return noteItem;
    }

    public void setNoteItem(TextView noteItem) {
        this.noteItem = noteItem;
    }
}
