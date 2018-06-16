package com.pshkrh.eton;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pshkrh.eton.Helper.DatabaseHelper;
import com.pshkrh.eton.Helper.SnackbarHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class EditActivity extends AppCompatActivity {

    public static String TITLE = "Title";
    public static String DESC = "Description";
    public static String DATE = "Date";
    public static String STAR = "Star";

    public int starred, itemID;
    public Context mContext = this;

    public String intentTitle, intentDescription, intentDate;

    DatabaseHelper mDatabaseHelper;

    //private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Raleway-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        final MaterialEditText title = findViewById(R.id.edit_title);
        title.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final MaterialEditText description = findViewById(R.id.edit_description);
        description.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        intentTitle = getIntent().getStringExtra(TITLE);
        intentDescription = getIntent().getStringExtra(DESC);
        intentDate = getIntent().getStringExtra(DATE);

        starred = getIntent().getIntExtra(STAR,0);

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
                    mDatabaseHelper.updateNote(editTitle,editDescription,itemID,starred);
                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                    intent.putExtra("editResult","Note Edited!");

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
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        if(starred==1){
            menu.findItem(R.id.edit_star).setIcon(R.drawable.star);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.edit_star:
                if(starred==0) {
                    starred = 1;
                    item.setIcon(R.drawable.star);
                    break;
                }
                else {
                    starred = 0;
                    item.setIcon(R.drawable.star_outline);
                    break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
