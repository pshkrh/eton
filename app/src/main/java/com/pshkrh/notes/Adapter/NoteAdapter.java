package com.pshkrh.notes.Adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pshkrh.notes.Model.Note;
import com.pshkrh.notes.R;

import java.util.List;

public class NoteAdapter extends
        RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context mContext;

        public TextView titleTextView, descriptionTextView, dateTextView;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.note_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.note_description);
            dateTextView = (TextView) itemView.findViewById(R.id.note_date);

            this.mContext = context;
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            //TODO: Intent to View Activity
            Snackbar.make(view,titleTextView.getText().toString(),Snackbar.LENGTH_SHORT).show();
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
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mNotes.size();
    }
}
