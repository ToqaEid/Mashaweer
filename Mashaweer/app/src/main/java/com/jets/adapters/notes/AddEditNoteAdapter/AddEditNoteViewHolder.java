package com.jets.adapters.notes.AddEditNoteAdapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.jets.mashaweer.R;

import java.util.ArrayList;

/**
 * Created by toqae on 3/27/2017.
 */

public class AddEditNoteViewHolder {
        private View convertedView;
        private CheckBox noteCheck;
        private EditText noteItem;
        private ImageButton cancelBtn;
        private String activityFlag;

        public AddEditNoteViewHolder(View view)
        {
            convertedView = view ;
        }

        public CheckBox getNoteCheck() {

            if(noteCheck == null) {
                noteCheck = (CheckBox) convertedView.findViewById(R.id.checkBox2);
            }

            return noteCheck;
        }

        public void setNoteCheck(CheckBox noteCheck) {
            this.noteCheck = noteCheck;
        }

        public EditText getNoteItem() {

            if(noteItem == null)
                noteItem = (EditText) convertedView.findViewById(R.id.note_edit_text);
            if(activityFlag.equals("edit")) {

               noteItem.setPaintFlags(noteItem.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            return noteItem;
        }

        public void setNoteItem(EditText noteItem) {
            this.noteItem = noteItem;
        }

        public ImageButton getCancelBtn() {
            if (cancelBtn == null) {
                cancelBtn = (ImageButton) convertedView.findViewById(R.id.cancel_checked_btn1);
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


