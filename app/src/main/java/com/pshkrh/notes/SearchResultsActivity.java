package com.pshkrh.notes;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.pshkrh.notes.Adapter.SearchAdapter;
import com.pshkrh.notes.Helper.DatabaseHelper;
import com.pshkrh.notes.Helper.SnackbarHelper;
import com.pshkrh.notes.Model.Note;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    Context mContext = this;
    public View parentView;
    public DatabaseHelper mDatabaseHelper;
    public SearchAdapter searchAdapter;

    public static String TAG = "SearchResultsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        parentView = findViewById(R.id.search_coordinator);
        mDatabaseHelper = new DatabaseHelper(this);

        String activityName = getString(R.string.search_results);
        SpannableString s = new SpannableString(activityName);
        s.setSpan(new TypefaceSpan(mContext, "Raleway-Medium.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setTitle(s);

        ArrayList<Note> searchedNotes = new ArrayList<Note>();
        searchAdapter = new SearchAdapter(this,searchedNotes);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(searchAdapter);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "Query from intent = " + query);
            //use the query to search your data somehow
            Cursor data = mDatabaseHelper.getSearchedData(query);
            if(data!=null){
                if(data.getCount() == 0){
                    SnackbarHelper.snackShort(parentView,"No results found!");
                }
                else{
                    while(data.moveToNext()){
                        String title = data.getString(1);
                        String description = data.getString(2);
                        String date = data.getString(3);
                        int starred = data.getInt(4);

                        Note insertNote = new Note(title,description,date,starred);
                        searchAdapter.add(insertNote);
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SearchResultsActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
