package com.jets.adapters.notes.checkednotes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jets.mashaweer.R;

import java.util.ArrayList;

/**
 * Created by toqae on 3/24/2017.
 */

public class CheckedNoteAdatper extends ArrayAdapter<String> {

    private ArrayList<String> notes;

    private Context context;

    public CheckedNoteAdatper(Context context, ArrayList<String> notes) {
        super(context, R.layout.list_item_unchecked_notes, R.id.note_text, notes);
        this.context = context;

        this.notes = notes;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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


        holder.getNoteItem().setText(notes.get(position));


        return  rowView;
    }

    @Override
    public int getCount() {
        Log.i("Tag size", String.valueOf(notes.size()));
        return notes.size();
    }
}

