package com.pshkrh.eton;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.pshkrh.eton.Adapter.SearchAdapter;
import com.pshkrh.eton.Helper.DatabaseHelper;
import com.pshkrh.eton.Helper.SnackbarHelper;
import com.pshkrh.eton.Model.Note;

import java.util.ArrayList;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SearchResultsActivity extends AppCompatActivity {

    Context mContext = this;
    public View parentView;
    public DatabaseHelper mDatabaseHelper;
    public SearchAdapter searchAdapter;

    public static String TAG = "SearchResultsActivity";
    public static String TITLE = "Title";
    public static String DESC = "Description";
    public static String DATE = "Date";
    public static String STAR = "Star";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        parentView = findViewById(R.id.search_coordinator);
        mDatabaseHelper = new DatabaseHelper(this);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Raleway-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        ArrayList<Note> searchedNotes = new ArrayList<Note>();
        searchAdapter = new SearchAdapter(this,searchedNotes);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(searchAdapter);

        handleIntent(getIntent());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note note = (Note)adapterView.getItemAtPosition(i);
                Intent intent = new Intent(SearchResultsActivity.this,ViewNoteActivity.class);
                intent.putExtra(TITLE,note.getTitle());
                intent.putExtra(DESC,note.getDescription());
                intent.putExtra(DATE,note.getDate());
                intent.putExtra(STAR,note.getStarred());
                startActivity(intent);
                finish();
            }
        });
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
