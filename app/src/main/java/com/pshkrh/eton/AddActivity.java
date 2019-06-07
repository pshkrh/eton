package com.pshkrh.eton;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pshkrh.eton.Helper.DatabaseHelper;
import com.pshkrh.eton.Helper.SnackbarHelper;
import com.pshkrh.eton.Model.Note;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AddActivity extends AppCompatActivity {

    int starred=0;

    public DatabaseHelper mDatabaseHelper;
    public View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Raleway-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        parentView = findViewById(R.id.add_coordinator);
        mDatabaseHelper = new DatabaseHelper(this);

        final MaterialEditText title = findViewById(R.id.add_title);
        title.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        final MaterialEditText description = findViewById(R.id.description);
        description.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        description.requestFocus();

        FloatingActionButton fab = findViewById(R.id.add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(title.length()==0){
                    title.setText(R.string.untitled);
                }
                if(description.length()!=0){
                    Note note = new Note(title.getText().toString(),description.getText().toString(),starred);
                    insert(note);
                    title.setText("");
                    description.setText("");

                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    intent.putExtra("addResult","Note Added!");
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
    public void onBackPressed() {
        saveNote();
        Intent intent = new Intent(AddActivity.this,MainActivity.class);
        intent.putExtra("saveOnBack","Note saved as draft!");
        startActivity(intent);
        finish();
    }

    /*
    Insert Data
     */

    public void insert(Note note){
        boolean isInsert = mDatabaseHelper.addData(note,0);

        if(isInsert){
            SnackbarHelper.snackLong(parentView,"Inserted data successfully!");
        }
        else{
            SnackbarHelper.snackShort(parentView,"Could not insert data.");
        }
    }

    public void saveNote(){
        MaterialEditText title = findViewById(R.id.add_title);
        MaterialEditText description = findViewById(R.id.description);
        if(title.length()==0){
            title.setText(R.string.untitled);
        }
        if(description.length()!=0){
            Note note = new Note(title.getText().toString(),description.getText().toString(),starred);
            insert(note);
            title.setText("");
            description.setText("");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
