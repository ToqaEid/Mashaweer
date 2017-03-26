package com.jets.adapters.notes.checkednotes;

import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jets.mashaweer.R;

/**
 * Created by toqae on 3/24/2017.
 */

public class CheckedNoteViewHolder {
    private View convertedView;
    private CheckBox noteCheck;
    private EditText noteItem;
    private ImageButton cancelBtn;
    private String activityFlag;

    public CheckedNoteViewHolder(View view)
    {
        convertedView = view ;
    }

    public CheckBox getNoteCheck() {

        if(noteCheck == null) {
            noteCheck = (CheckBox) convertedView.findViewById(R.id.note_check);
        }

        return noteCheck;
    }

    public void setNoteCheck(CheckBox noteCheck) {
        this.noteCheck = noteCheck;
    }

    public EditText getNoteItem() {

        if(noteItem == null)
            noteItem = (EditText) convertedView.findViewById(R.id.note_text);
        noteItem.setPaintFlags(noteItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        return noteItem;
    }

    public void setNoteItem(EditText noteItem) {
        this.noteItem = noteItem;
    }

    public ImageButton getCancelBtn() {
        if (cancelBtn == null) {
            cancelBtn = (ImageButton) convertedView.findViewById(R.id.cancel_checked_btn);
        }
        return cancelBtn;

    }

    public void setCancelBtn(ImageButton cancelBtn) {
        this.cancelBtn = cancelBtn;
    }

    public void setActivityFlag(String activityFlag){
        this.activityFlag = activityFlag;
    }
}


