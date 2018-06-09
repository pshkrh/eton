package com.pshkrh.notes;

import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pshkrh.notes.Adapter.DeletedNoteAdapter;
import com.pshkrh.notes.Adapter.NoteAdapter;
import com.pshkrh.notes.Helper.DatabaseHelper;
import com.pshkrh.notes.Helper.SnackbarHelper;
import com.pshkrh.notes.Model.Note;
import com.pshkrh.notes.TypefaceSpan;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class BinActivity extends AppCompatActivity {

    public Context mContext = this;

    public static String TAG = "BinActivity";
    public ArrayList<Note> deletedNotes = new ArrayList<Note>();
    public DatabaseHelper mDatabaseHelper;
    public View parentView;

    public String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin);
        String activityName = getString(R.string.bin);
        SpannableString s = new SpannableString(activityName);
        s.setSpan(new TypefaceSpan(mContext, "Raleway-Medium.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setTitle(s);

        //Initialise DatabaseHelper
        mDatabaseHelper = new DatabaseHelper(this);
        parentView = findViewById(R.id.bin_coordinator);

        result = getIntent().getStringExtra("result");

        if(result!=null && !result.equals("")){
            SnackbarHelper.snackLong(parentView,"Note Deleted!");
        }


        // RecyclerView Binding
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.bin_recycler);

        populateBinRecycler();

        final DeletedNoteAdapter deletedNoteAdapter = new DeletedNoteAdapter(deletedNotes);
        recyclerView.setAdapter(deletedNoteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Item Decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // Item Animator
        recyclerView.setItemAnimator(new SlideInUpAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                FloatingActionButton binFab = (FloatingActionButton)findViewById(R.id.bin_fab);
                if (dy > 0)
                    binFab.hide();
                else if (dy < 0)
                    binFab.show();
            }
        });

        if(deletedNoteAdapter.getItemCount()==0){
            RelativeLayout emptyList = (RelativeLayout)findViewById(R.id.bin_placeholder);
            emptyList.setVisibility(View.VISIBLE);
            FloatingActionButton binFab = (FloatingActionButton)findViewById(R.id.bin_fab);
            binFab.setVisibility(View.GONE);

        }

        final FloatingActionButton binFab = (FloatingActionButton)findViewById(R.id.bin_fab);
        binFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(BinActivity.this)
                        .title(R.string.delete_all_notes)
                        .content(R.string.delete_all_sure)
                        .positiveText(R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                mDatabaseHelper.deleteAll();
                                SnackbarHelper.snackShort(parentView,"Deleted All Notes!");
                                deletedNotes.clear();
                                deletedNoteAdapter.notifyDataSetChanged();
                                RelativeLayout placeholder = (RelativeLayout)findViewById(R.id.bin_placeholder);
                                placeholder.setVisibility(View.VISIBLE);
                                binFab.setVisibility(View.GONE);
                            }
                        })
                        .negativeText(R.string.no)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .icon(getResources().getDrawable(R.drawable.comment_question,getTheme()))
                        .typeface("Raleway-Medium.ttf","Raleway-Regular.ttf")
                        .show();
            }
        });

    }

    private void populateBinRecycler() {
        Log.d(TAG, "populateBinRecycler: Displaying all deleted notes in the RecyclerView");

        Cursor data = mDatabaseHelper.getDeletedData();
        while(data.moveToNext()){
            String title = data.getString(1);
            String description = data.getString(2);
            String date = data.getString(3);
            int starred = data.getInt(4);

            Note insertNote = new Note(title,description,date,starred);
            deletedNotes.add(insertNote);

        }
    }

    public void setRecycler(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.bin_recycler);
        DeletedNoteAdapter deletedNoteAdapter = new DeletedNoteAdapter(deletedNotes);
        recyclerView.setAdapter(deletedNoteAdapter);
        deletedNoteAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(BinActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BinActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
