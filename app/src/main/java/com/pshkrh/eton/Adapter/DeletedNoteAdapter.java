package com.pshkrh.eton.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pshkrh.eton.Helper.DatabaseHelper;
import com.pshkrh.eton.Helper.SnackbarHelper;
import com.pshkrh.eton.MainActivity;
import com.pshkrh.eton.Model.Note;
import com.pshkrh.eton.R;
import com.pshkrh.eton.BinActivity;
import com.pshkrh.eton.ViewNoteActivity;

import java.util.List;

public class DeletedNoteAdapter extends RecyclerView.Adapter<DeletedNoteAdapter.ViewHolder> {

    public static String TAG = "DeletedNoteAdapter";
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

            titleTextView = (TextView) itemView.findViewById(R.id.deleted_note_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.deleted_note_description);
            dateTextView = (TextView) itemView.findViewById(R.id.deleted_note_date);
            star = (ImageView)itemView.findViewById(R.id.deleted_starred_note);

            this.mContext = context;
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(final View view) {
            Context context = itemView.getContext();
            int position = getAdapterPosition(); // gets item position

            new MaterialDialog.Builder(context)
                    .title(R.string.del_res)
                    .content(R.string.delete_or_restore)
                    .positiveText(R.string.delete_forever)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            Context context = itemView.getContext();
                            DatabaseHelper mDatabaseHelper = new DatabaseHelper(context);
                            int id=-1;
                            String date = dateTextView.getText().toString();
                            Cursor data = mDatabaseHelper.getDeletedItemID(date);
                            while(data.moveToNext()){
                                id = data.getInt(0);
                            }

                            if(id <= -1){
                                Log.d(TAG, "DeletedNoteAdapter: onClick Positive: ID = -1");
                            }

                            mDatabaseHelper.deleteBinNote(id);

                            Intent intent = new Intent(context, BinActivity.class);
                            intent.putExtra("result","Note Restored Successfully!");

                            // Clear the back stack of activities
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                            //Start the activity
                            context.startActivity(intent);
                        }
                    })
                    .negativeText(R.string.restore)
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            Context context = itemView.getContext();
                            DatabaseHelper mDatabaseHelper = new DatabaseHelper(context);

                            String title = titleTextView.getText().toString();
                            String description = descriptionTextView.getText().toString();
                            String date = dateTextView.getText().toString();
                            int starred = 0;
                            if(star.getVisibility() == View.VISIBLE){
                                starred = 1;
                            }

                            Note note = new Note(title,description,date,starred);
                            mDatabaseHelper.addData(note,1);

                            int id=-1;
                            Cursor data = mDatabaseHelper.getDeletedItemID(date);
                            while(data.moveToNext()){
                                id = data.getInt(0);
                            }

                            if(id <= -1){
                                Log.d(TAG, "DeletedNoteAdapter: onClick Item: ID = -1");
                            }

                            mDatabaseHelper.deleteBinNote(id);

                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("result","Note Restored Successfully!");

                            // Clear the back stack of activities
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                            //Start the activity
                            context.startActivity(intent);
                        }
                    })
                    .icon(itemView.getResources().getDrawable(R.drawable.comment_question,context.getTheme()))
                    .typeface("Raleway-Medium.ttf","Raleway-Regular.ttf")
                    .show();
        }

    }

    private List<Note> deletedNotes;

    public DeletedNoteAdapter(List<Note> notes) {
        deletedNotes = notes;
    }

    @Override
    public DeletedNoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View binView = inflater.inflate(R.layout.deleted_item_note, parent, false);

        // Return a new holder instance
        DeletedNoteAdapter.ViewHolder viewHolder = new DeletedNoteAdapter.ViewHolder(context,binView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(DeletedNoteAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Note note = deletedNotes.get(position);

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
        return deletedNotes.size();
    }

}
