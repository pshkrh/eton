package com.pshkrh.notes;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pshkrh.notes.Adapter.NoteAdapter;
import com.pshkrh.notes.Helper.DatabaseHelper;
import com.pshkrh.notes.Helper.SnackbarHelper;
import com.pshkrh.notes.Model.Note;

import java.util.ArrayList;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String TAG = "MainActivity";
    public ArrayList<Note> notes = new ArrayList<Note>();
    public int starred=0;

    public DatabaseHelper mDatabaseHelper;
    public View parentView;

    public String binResult="";
    public String deleteResult="";
    public String addResult="";
    public String editResult="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.RalewayTextAppearance);
        setSupportActionBar(toolbar);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Raleway-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        mDatabaseHelper = new DatabaseHelper(this);
        parentView = findViewById(R.id.coordinator);

        intentResultCheck();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();

        for (int i=0;i<menu.size();i++) {
            MenuItem mi = menu.getItem(i);

            //To apply fonts in the SubMenus
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //Custom method
            applyFontToMenuItem(mi);
        }


        // RecyclerView Binding
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_recycler);

        populateRecycler();

        NoteAdapter noteAdapter;noteAdapter = new NoteAdapter(notes);
        recyclerView.setAdapter(noteAdapter);
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
                FloatingActionButton addFab = (FloatingActionButton)findViewById(R.id.fab);
                if (dy > 0)
                    addFab.hide();
                else if (dy < 0)
                    addFab.show();
            }
        });

        if(noteAdapter.getItemCount()==0){
            RelativeLayout emptyList = (RelativeLayout)findViewById(R.id.placeholder_view);
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    private void intentResultCheck(){
        binResult = getIntent().getStringExtra("result");
        deleteResult = getIntent().getStringExtra("deleteResult");
        addResult = getIntent().getStringExtra("addResult");
        editResult = getIntent().getStringExtra("editResult");

        if(binResult!=null && !binResult.equals("")){
            SnackbarHelper.snackLong(parentView,binResult);
        }

        if(deleteResult!=null && !deleteResult.equals("")){
            SnackbarHelper.snackLong(parentView,deleteResult);
        }

        if(addResult!=null && !addResult.equals("")){
            SnackbarHelper.snackLong(parentView,addResult);
        }

        if(editResult!=null && !editResult.equals("")){
            SnackbarHelper.snackLong(parentView,editResult);
        }
    }

    private void populateRecycler() {
        Log.d(TAG, "populateRecycler: Displaying data in the RecyclerView");

        Cursor data = mDatabaseHelper.getData();
        while(data.moveToNext()){
            String title = data.getString(1);
            String description = data.getString(2);
            String date = data.getString(3);
            int starred = data.getInt(4);

            Note insertNote = new Note(title,description,date,starred);
            notes.add(insertNote);
        }
    }

    public void displayAll(){
        Log.d(TAG, "displayAll: Displaying all notes in the RecyclerView");
        notes.clear();
        Cursor data = mDatabaseHelper.getData();
        while(data.moveToNext()){
            String title = data.getString(1);
            String description = data.getString(2);
            String date = data.getString(3);
            int starred = data.getInt(4);

            Note insertNote = new Note(title,description,date,starred);
            notes.add(insertNote);

            setRecycler();
        }
    }

    public void displayStarred(){
        Log.d(TAG, "displayStarred: Displaying only starred items in the RecyclerView");
        notes.clear();
        Cursor data = mDatabaseHelper.getStarredData();
        if(data.getCount()==0){
            SnackbarHelper.snackShort(parentView,"No starred items!");
        }
        else {
            while (data.moveToNext()) {
                String title = data.getString(1);
                String description = data.getString(2);
                String date = data.getString(3);
                int starred = data.getInt(4);

                Note insertNote = new Note(title, description, date, starred);
                notes.add(insertNote);

                setRecycler();

            }
        }
    }

    public void displayAlphabeticallySorted() {
        Log.d(TAG, "displayAlphabeticallySorted: Displaying alphabetically sorted items in the RecyclerView");
        notes.clear();
        Cursor data = mDatabaseHelper.getSortedData("alphabetical");
        while (data.moveToNext()) {
            String title = data.getString(1);
            String description = data.getString(2);
            String date = data.getString(3);
            int starred = data.getInt(4);

            Note insertNote = new Note(title, description, date, starred);
            notes.add(insertNote);

            setRecycler();
        }
    }

    public void displayAscendingSorted() {
        Log.d(TAG, "displayAscendingSorted: Displaying ascending sorted items in the RecyclerView");
        notes.clear();
        Cursor data = mDatabaseHelper.getSortedData("ascending");
        while (data.moveToNext()) {
            String title = data.getString(1);
            String description = data.getString(2);
            String date = data.getString(3);
            int starred = data.getInt(4);

            Note insertNote = new Note(title, description, date, starred);
            notes.add(insertNote);

            setRecycler();
        }
    }

    public void displayDescendingSorted(){
        Log.d(TAG, "displayDescendingSorted: Displaying descending sorted items in the RecyclerView");
        notes.clear();
        Cursor data = mDatabaseHelper.getSortedData("descending");
        while (data.moveToNext()) {
            String title = data.getString(1);
            String description = data.getString(2);
            String date = data.getString(3);
            int starred = data.getInt(4);

            Note insertNote = new Note(title, description, date, starred);
            notes.add(insertNote);

            setRecycler();
        }
    }

    public void setRecycler(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        NoteAdapter noteAdapter = new NoteAdapter(notes);
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultsActivity.class)));
        searchView.setQueryHint(getResources().getString(R.string.search_hint));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_star:
                if(starred==0) {
                    starred = 1;
                    item.setIcon(R.drawable.star);
                    notes.clear();
                    displayStarred();
                }
                else {
                    starred = 0;
                    item.setIcon(R.drawable.star_outline);
                    notes.clear();
                    displayAll();
                }
                break;

            case R.id.action_sort_alphabetical:
                displayAlphabeticallySorted();
                break;

            case R.id.action_sort_ascending:
                displayAscendingSorted();
                break;

            case R.id.action_sort_descending:
                displayDescendingSorted();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // set item as selected to persist highlight
        //item.setChecked(true);

        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            Snackbar.make(findViewById(R.id.coordinator),R.string.already_here,Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.nav_bin) {
            Intent intent = new Intent(MainActivity.this, BinActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_export) {
            Snackbar.make(findViewById(R.id.coordinator),R.string.coming_soon,Snackbar.LENGTH_LONG).show();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_contact) {
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.setType("vnd.android.cursor.item/email");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"dev@pshkrh.com"});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback / Query regarding Notes");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(emailIntent, "Send mail using..."));

        } else if (id == R.id.nav_settings) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}
