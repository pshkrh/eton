package com.pshkrh.notes;

import android.Manifest;
import android.animation.Animator;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pshkrh.notes.Adapter.NoteAdapter;
import com.pshkrh.notes.Helper.DatabaseHelper;
import com.pshkrh.notes.Helper.SnackbarHelper;
import com.pshkrh.notes.Model.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String TAG = "MainActivity";
    public static final int PERMISSIONS_REQUEST_WRITE_STORAGE = 1;

    public ArrayList<Note> notes = new ArrayList<Note>();
    public int starred=0;

    public DatabaseHelper mDatabaseHelper;
    public View parentView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    public String binResult="";
    public String deleteResult="";
    public String addResult="";
    public String editResult="";

    private int clickedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.RalewayTextAppearance);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sortPref = sharedPref.getString(SettingsActivity.KEY_SORT, "");

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

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView)
            {
                super.onDrawerClosed(drawerView);

                if(clickedItem != 0)
                {
                    handleNavigationClick();
                }
            }

        };
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

        Log.d(TAG,"Sort Preference = " + sortPref);
        switch(sortPref){

            case "0":
                displayAlphabeticallySorted();
                break;

            case "1":
                displayAscendingSorted();
                break;

            case "2":
                displayDescendingSorted();
                break;
        }

        NoteAdapter noteAdapter;
        noteAdapter = new NoteAdapter(notes);
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
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
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
        clickedItem = item.getItemId();
        drawer.closeDrawers();
        return true;
    }

    private void handleNavigationClick(){
        switch(clickedItem){
            case R.id.nav_notes:
                Snackbar.make(findViewById(R.id.coordinator),R.string.already_here,Snackbar.LENGTH_LONG).show();
                break;

            case R.id.nav_bin:
                Intent intent = new Intent(MainActivity.this, BinActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.nav_export:
                if(storagePermissionChecker()){
                    new MaterialDialog.Builder(this)
                            .title(R.string.export_notes)
                            .content(R.string.export_info_no_perm)
                            .positiveText(R.string.export)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    //permissionsPrompt();
                                    export(notes);
                                }
                            })
                            .negativeText(R.string.cancel)
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
                else{
                    new MaterialDialog.Builder(this)
                            .title(R.string.export_notes)
                            .content(R.string.export_info)
                            .positiveText(R.string.export)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    permissionsPrompt();
                                }
                            })
                            .negativeText(R.string.cancel)
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
                break;

            case R.id.nav_about:
                intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_contact:
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"dev@pshkrh.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback / Query regarding Notes");
                startActivity(Intent.createChooser(intent, "Send mail using..."));
                break;

            case R.id.nav_settings:
                intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        clickedItem = 0;

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

    private void export(ArrayList<Note> notes){
        //Prepare String for all notes
        StringBuilder content = new StringBuilder();
        for(Note tempNote : notes){
            content.append(tempNote.getTitle()).append("\n\n")
                    .append(tempNote.getDescription())
                    .append("\n\n")
                    .append(tempNote.getDate())
                    .append("\n\n")
                    .append("---------------------------------------------")
                    .append("\n\n");
        }

        String fileContent = content.toString();

        //Write file to internal memory
        File file;
        FileOutputStream outputStream;
        try {
            Log.d(TAG,"export: Writing file");
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.ENGLISH);
            String fileName = "Notes-" + sdf.format(date) + ".txt";
            file = new File(Environment.getExternalStorageDirectory(), fileName);
            outputStream = new FileOutputStream(file);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
            SnackbarHelper.snackLong(parentView,"Notes Exported to Internal Storage!\n" + "File Name: " + fileName);
            Log.d(TAG,"export: File Written");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"export: File write failed");
            SnackbarHelper.snackShort(parentView,"File Export Failed");
        }
    }

    private void permissionsPrompt() {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_STORAGE);
    }

    private boolean storagePermissionChecker(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"Permission OK");
                    export(notes);
                } else {
                    Log.d(TAG,"Permission NOT OK");
                    SnackbarHelper.snackShort(parentView,"The permission needs to be granted for export to work!");
                }
            }
        }
    }

}
