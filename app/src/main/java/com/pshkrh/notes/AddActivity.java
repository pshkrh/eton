package com.pshkrh.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pshkrh.notes.Helper.DatabaseHelper;
import com.pshkrh.notes.Helper.SnackbarHelper;
import com.pshkrh.notes.Model.Note;

public class AddActivity extends AppCompatActivity {

    Context mContext = this;
    int imp=0;

    public DatabaseHelper mDatabaseHelper;
    public View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        String activityName = getString(R.string.add_note);
        SpannableString s = new SpannableString(activityName);
        s.setSpan(new TypefaceSpan(mContext, "Raleway-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        parentView = findViewById(R.id.add_coordinator);
        mDatabaseHelper = new DatabaseHelper(this);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setTitle(s);

        final TextInputEditText title = findViewById(R.id.add_title);
        final TextInputEditText description = findViewById(R.id.description);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");

        title.setTypeface(font);
        description.setTypeface(font);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.length()!=0 && description.length()!=0){
                    Note note = new Note(title.getText().toString(),description.getText().toString());
                    insert(note);
                    title.setText("");
                    description.setText("");

                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    SnackbarHelper.snackLong(view, "Note is empty!");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.add_star:
                if(imp==0) {
                    imp = 1;
                    item.setIcon(R.drawable.star);
                    break;
                }
                else {
                    imp = 0;
                    item.setIcon(R.drawable.star_outline);
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Insert Data
     */

    public void insert(Note note){
        boolean isInsert = mDatabaseHelper.addData(note);

        if(isInsert){
            SnackbarHelper.snackLong(parentView,"Inserted data successfully!");
        }
        else{
            SnackbarHelper.snackShort(parentView,"Could not insert data.");
        }
    }
}
