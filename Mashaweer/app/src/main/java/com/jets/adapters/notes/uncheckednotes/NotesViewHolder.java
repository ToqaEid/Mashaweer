package com.jets.adapters.notes.uncheckednotes;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jets.mashaweer.R;

/**
 * Created by toqae on 3/24/2017.
 */

public class NotesViewHolder {

    View convertedView;
    CheckBox noteCheck;
    TextView noteItem;
    ImageButton cancelBtn;
    private String activityFlag;

    public NotesViewHolder(View view)
    {
        convertedView = view ;
    }

    public CheckBox getNoteCheck() {

        if(noteCheck == null)
            noteCheck = (CheckBox) convertedView.findViewById(R.id.note_check);

        return noteCheck;
    }

    public void setActivityFlag(String activityFlag){
        this.activityFlag = activityFlag;
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

    public ImageButton getCancelBtn() {
        if (cancelBtn == null) {
            cancelBtn = (ImageButton) convertedView.findViewById(R.id.cancel_unchecked_btn);
        }
        return cancelBtn;

    }

    public void setCancelBtn(ImageButton cancelBtn) {
        this.cancelBtn = cancelBtn;
    }
}
