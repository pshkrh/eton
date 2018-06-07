package com.pshkrh.notes;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;

public class AddActivity extends AppCompatActivity {

    Context mContext = this;
    int imp=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        String activityName = getString(R.string.add_note);
        SpannableString s = new SpannableString(activityName);
        s.setSpan(new TypefaceSpan(mContext, "Raleway-Regular.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setTitle(s);

        TextInputEditText title = findViewById(R.id.add_title);
        TextInputEditText note = findViewById(R.id.description);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Medium.ttf");

        title.setTypeface(font);
        note.setTypeface(font);
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
