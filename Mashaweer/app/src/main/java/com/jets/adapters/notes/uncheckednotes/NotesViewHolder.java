package com.jets.adapters.notes.uncheckednotes;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jets.mashaweer.R;

/**
 * Created by toqae on 3/24/2017.
 */

public class NotesViewHolder {

    private View convertedView;
    private CheckBox noteCheck;
    private EditText noteItem;
    private ImageButton cancelBtn;
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

    public EditText getNoteItem() {

        if(noteItem == null)
            noteItem = (EditText) convertedView.findViewById(R.id.note_text);

        return noteItem;
    }

    public void setNoteItem(EditText noteItem) {
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
