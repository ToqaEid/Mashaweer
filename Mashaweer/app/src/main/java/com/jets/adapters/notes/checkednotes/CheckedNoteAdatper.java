package com.jets.adapters.notes.checkednotes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jets.mashaweer.R;
import com.jets.mashaweer.TripAddActivity;

import java.util.ArrayList;

/**
 * Created by toqae on 3/24/2017.
 */

public class CheckedNoteAdatper extends ArrayAdapter<String> {

    private ArrayList<String> notes;
    private String activityFlag;

    private Context context;

    public CheckedNoteAdatper(Context context, ArrayList<String> notes) {
        super(context, R.layout.list_item_unchecked_notes, R.id.note_text, notes);
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
        CheckedNoteViewHolder holder;


        if (rowView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = layoutInflater.inflate(R.layout.list_item_checked_notes, parent, false);

            holder = new CheckedNoteViewHolder(rowView);

            rowView.setTag(holder);

        }
        else{

            holder = (CheckedNoteViewHolder) rowView.getTag();
        }


        holder.setActivityFlag(activityFlag);

        holder.getNoteItem().setText(   notes.get(position)   );

        if (!activityFlag.equals("edit")){
            holder.getCancelBtn().setVisibility(View.GONE);

        }else{
            holder.getCancelBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TripAddActivity tripAddActivity = (TripAddActivity) context;
                    tripAddActivity.removeFromUncheckedList(position);
                }
            });
        }

        return  rowView;
    }

    @Override
    public int getCount() {
        Log.i("Tag size", String.valueOf(notes.size()));
        return notes.size();
    }
}

