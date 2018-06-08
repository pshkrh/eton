package com.pshkrh.notes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.pshkrh.notes.Helper.SnackbarHelper;

public class ViewNoteActivity extends AppCompatActivity {

    public static String TITLE = "Title";
    public static String DESC = "Description";
    public static String DATE = "Date";

    public Context mContext = this;

    public String title,description,date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        String activityName = getString(R.string.view_note);
        SpannableString s = new SpannableString(activityName);
        s.setSpan(new TypefaceSpan(mContext, "Raleway-Medium.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Update the action bar title with the TypefaceSpan instance
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setTitle(s);

        title = getIntent().getStringExtra(TITLE);
        description = getIntent().getStringExtra(DESC);
        date = getIntent().getStringExtra(DATE);

        TextView titleTextView = (TextView)findViewById(R.id.view_note_title);
        TextView descriptionTextView = (TextView)findViewById(R.id.view_note_description);
        TextView dateTextView = (TextView)findViewById(R.id.view_note_date);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        dateTextView.setText(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.view_edit:
                Intent intent = new Intent(ViewNoteActivity.this,EditActivity.class);
                intent.putExtra(TITLE,title);
                intent.putExtra(DESC,description);
                startActivity(intent);
                break;

            case R.id.view_delete:
                //TODO: Delete Logic Here.

            case R.id.view_copy_title:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Note Title", title);
                if(clipboard!=null)
                    clipboard.setPrimaryClip(clip);
                SnackbarHelper.snackShort(findViewById(R.id.view_coordinator),"Title copied to clipboard!");
                break;

            case R.id.view_copy_description:
                clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clip = ClipData.newPlainText("Note Description", description);
                if(clipboard!=null)
                    clipboard.setPrimaryClip(clip);
                SnackbarHelper.snackShort(findViewById(R.id.view_coordinator),"Description copied to clipboard!");
                break;

            case R.id.view_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String sharedContent = title + "\n\n" + description;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sharedContent);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
        }
        return super.onOptionsItemSelected(item);
    }
}
