package com.pshkrh.notes.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pshkrh.notes.Model.Note;
import com.pshkrh.notes.R;
import com.pshkrh.notes.ViewNoteActivity;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends
        RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    public static String TITLE = "Title";
    public static String DESC = "Description";
    public static String DATE = "Date";
    public static String STAR = "Star";

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context mContext;

        public TextView titleTextView, descriptionTextView, dateTextView;
        public ImageView star;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.note_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.note_description);
            dateTextView = (TextView) itemView.findViewById(R.id.note_date);
            star = (ImageView)itemView.findViewById(R.id.starred_note);

            this.mContext = context;
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            Context context = itemView.getContext();
            int position = getAdapterPosition(); // gets item position
            Intent intent = new Intent(context, ViewNoteActivity.class);
            intent.putExtra(TITLE, titleTextView.getText().toString());
            intent.putExtra(DESC, descriptionTextView.getText().toString());
            intent.putExtra(DATE, dateTextView.getText().toString());

            if(star.getVisibility() == View.VISIBLE){
                intent.putExtra(STAR,1);
            }
            else{
                intent.putExtra(STAR,0);
            }
            context.startActivity(intent);
        }

    }

    private List<Note> mNotes;

    public NoteAdapter(List<Note> notes) {
        mNotes = notes;
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_note, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(context,contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Note note = mNotes.get(position);

        // Set item views based on your views and data model
        TextView title = viewHolder.titleTextView;
        title.setText(note.getTitle());

        TextView description = viewHolder.descriptionTextView;
        description.setText(note.getDescription());

        TextView date = viewHolder.dateTextView;
        date.setText(note.getDate());

        ImageView star = viewHolder.star;
        int check = note.getStarred();
        if(check == 1){
            star.setVisibility(View.VISIBLE);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mNotes.size();
    }

}
