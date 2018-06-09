package com.pshkrh.notes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pshkrh.notes.Helper.DatabaseHelper;
import com.pshkrh.notes.Helper.SnackbarHelper;

public class EditActivity extends AppCompatActivity {

    public static String TITLE = "Title";
    public static String DESC = "Description";
    public static String DATE = "Date";

    public int imp=0, itemID;
    public Context mContext = this;

    public String intentTitle, intentDescription, intentDate;

    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        String activityName = getString(R.string.edit_note);
        SpannableString s = new SpannableString(activityName);
        s.setSpan(new TypefaceSpan(mContext, "Raleway-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setTitle(s);

        final TextInputEditText title = findViewById(R.id.edit_title);
        final TextInputEditText description = findViewById(R.id.edit_description);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");

        title.setTypeface(font);
        description.setTypeface(font);

        intentTitle = getIntent().getStringExtra(TITLE);
        intentDescription = getIntent().getStringExtra(DESC);
        intentDate = getIntent().getStringExtra(DATE);

        title.setText(intentTitle);
        description.setText(intentDescription);

        mDatabaseHelper = new DatabaseHelper(this);
        Cursor data = mDatabaseHelper.getItemID(intentDate);
        itemID = -1;
        while(data.moveToNext()){
            itemID = data.getInt(0);
        }
        if(itemID <= -1){
            SnackbarHelper.snackShort(findViewById(R.id.edit_coordinator), "No ID associated with that Note");
        }

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.edit_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editTitle = title.getText().toString();
                String editDescription = description.getText().toString();

                if(!editTitle.equals("") && !editDescription.equals("")){
                    mDatabaseHelper.updateNote(editTitle,editDescription,itemID);
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);

                    // Clear the back stack of activities
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    //Start the activity and finish the current one
                    startActivity(intent);
                    finish();
                }
                else{
                    SnackbarHelper.snackShort(view,"Title or description cannot be empty!");
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
}
