package com.pshkrh.notes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pshkrh.notes.Model.Note;
import com.pshkrh.notes.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<Note> {
    public SearchAdapter(Context context, ArrayList<Note> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Note note = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.searched_item_note, parent, false);
        }
        // Lookup view for data population
        TextView title = convertView.findViewById(R.id.searched_note_title);
        TextView description = convertView.findViewById(R.id.searched_note_description);
        TextView date = convertView.findViewById(R.id.searched_note_date);
        ImageView star = convertView.findViewById(R.id.searched_starred_note);
        // Populate the data into the template view using the data object
        title.setText(note.getTitle());
        description.setText(note.getDescription());
        date.setText(note.getDate());
        if(note.getStarred() == 1){
            star.setVisibility(View.VISIBLE);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}