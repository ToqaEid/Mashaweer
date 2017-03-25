package com.jets.adapters.notes.uncheckednotes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jets.classes.Note;
import com.jets.mashaweer.R;

import java.util.ArrayList;

/**
 * Created by toqae on 3/24/2017.
 */

public class NotesAdapter extends ArrayAdapter<String> {

    private ArrayList<String> notes;

    private Context context;

    public NotesAdapter(Context context, ArrayList<String> notes) {
        super(context, R.layout.content_unchecked_notes, R.id.note_text, notes);
        this.context = context;

        this.notes = notes;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        NotesViewHolder holder;


        if (rowView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = layoutInflater.inflate(R.layout.content_unchecked_notes, parent, false);

            holder = new NotesViewHolder(rowView);

            rowView.setTag(holder);

        }
        else{

            holder = (NotesViewHolder) rowView.getTag();
        }


        holder.getNoteItem().setText(   notes.get(position)   );


        return  rowView;
    }

    @Override
    public int getCount() {
        Log.i("Tag size", String.valueOf(notes.size()));
        return notes.size();
    }
}

