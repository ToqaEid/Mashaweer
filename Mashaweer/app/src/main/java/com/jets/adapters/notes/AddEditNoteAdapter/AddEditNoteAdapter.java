package com.jets.adapters.notes.AddEditNoteAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.jets.adapters.notes.checkednotes.CheckedNoteViewHolder;
import com.jets.mashaweer.R;
import com.jets.mashaweer.TripAddActivity;

import java.util.ArrayList;

/**
 * Created by toqae on 3/27/2017.
 */

public class AddEditNoteAdapter extends ArrayAdapter<String> {

    private ArrayList<String> notes;
    private String activityFlag;

    private Context context;

    public AddEditNoteAdapter(Context context, ArrayList<String> notes) {
        super(context, R.layout.list_item_notes_checked, R.id.note_text, notes);
        this.context = context;

        this.notes = notes;
    }

    public void setActivityFlag(String flag){
        activityFlag = flag;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        final AddEditNoteViewHolder holder;


        if (rowView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = layoutInflater.inflate(R.layout.list_item_notes_checked, parent, false);

            holder = new AddEditNoteViewHolder(rowView);

            rowView.setTag(holder);

        }
        else{

            holder = (AddEditNoteViewHolder) rowView.getTag();
        }


        holder.setActivityFlag(activityFlag);

        holder.getNoteItem().setText(   notes.get(position)   );

        holder.getNoteItem().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    final EditText Caption = (EditText) v;
                    notes.set(position,Caption.getText().toString());
                    holder.getNoteItem().setText(   notes.get(position)   );
                }
            }
        });
        holder.getCancelBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripAddActivity tripAddActivity = (TripAddActivity) context;
                if(activityFlag.equals("add")) {
                    tripAddActivity.removeFromUncheckedList(position);
                }else{
                    tripAddActivity.removeFromcheckedList(position);
                }
            }
        });

        if(activityFlag.equals("edit")) {
            holder.getNoteCheck().setChecked(true);
        }
        return  rowView;
    }

    @Override
    public int getCount() {
        Log.i("Tag size", String.valueOf(notes.size()));
        return notes.size();
    }
}

