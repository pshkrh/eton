package com.pshkrh.eton;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pshkrh.eton.Helper.DatabaseHelper;
import com.pshkrh.eton.Helper.SnackbarHelper;
import com.pshkrh.eton.Model.Note;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ViewNoteActivity extends AppCompatActivity {

    public static String TAG = "ViewNoteActivity";
    public static String TITLE = "Title";
    public static String DESC = "Description";
    public static String DATE = "Date";
    public static String STAR = "Star";

    public Context mContext = this;

    public String title,description,date;
    public int starred;

    public DatabaseHelper mDatabaseHelper;
    public int itemID;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Raleway-Medium.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        title = getIntent().getStringExtra(TITLE);
        description = getIntent().getStringExtra(DESC);
        date = getIntent().getStringExtra(DATE);
        starred = getIntent().getIntExtra(STAR,0);

        if(starred==1){
            ImageView starImage = (ImageView)findViewById(R.id.starred_image);
            starImage.setVisibility(View.VISIBLE);
        }

        TextView titleTextView = (TextView)findViewById(R.id.view_note_title);
        TextView descriptionTextView = (TextView)findViewById(R.id.view_note_description);
        TextView dateTextView = (TextView)findViewById(R.id.view_note_date);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        dateTextView.setText(date);

        mDatabaseHelper = new DatabaseHelper(this);
        Cursor data = mDatabaseHelper.getItemID(date);
        itemID = -1;
        while(data.moveToNext()){
            itemID = data.getInt(0);
        }
        if(itemID <= -1){
            SnackbarHelper.snackShort(findViewById(R.id.edit_coordinator), "No ID associated with that Note");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_note, menu);

        //Apply Raleway font to Sort menu
        MenuItem menuItem = menu.getItem(2);
        SubMenu subMenu = menuItem.getSubMenu();
        if (subMenu!=null && subMenu.size() >0 ) {
            for (int j=0; j <subMenu.size();j++) {
                MenuItem subMenuItem = subMenu.getItem(j);
                applyFontToMenuItem(subMenuItem);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.view_edit:
                Intent intent = new Intent(ViewNoteActivity.this,EditActivity.class);
                intent.putExtra(TITLE,title);
                intent.putExtra(DESC,description);
                intent.putExtra(DATE, date);
                intent.putExtra(STAR,starred);
                startActivity(intent);
                break;

            case R.id.view_delete:
                new MaterialDialog.Builder(this)
                        .title(R.string.delete_note)
                        .content(R.string.are_you_sure)
                        .positiveText(R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                Note note = new Note(title,description,date,starred);
                                boolean res = mDatabaseHelper.addDeletedData(note);
                                if(!res){
                                    Log.d(TAG,"Adding to bin table failed");
                                }
                                mDatabaseHelper.deleteNote(itemID);
                                Intent intent = new Intent(ViewNoteActivity.this, MainActivity.class);
                                intent.putExtra("deleteResult","Note has been moved to Bin");

                                //Start the activity and finish the current one
                                startActivity(intent);
                                finish();
                            }
                        })
                        .negativeText(R.string.no)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .icon(getResources().getDrawable(R.drawable.comment_question,getTheme()))
                        .typeface("Raleway-Medium.ttf","Raleway-Regular.ttf")
                        .show();
                        break;

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

            case R.id.view_copy_whole:
                String wholeNote = title + "\n\n" + description;
                clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clip = ClipData.newPlainText("Whole Note", wholeNote);
                if(clipboard!=null)
                    clipboard.setPrimaryClip(clip);
                SnackbarHelper.snackShort(findViewById(R.id.view_coordinator),"Note copied to clipboard!");
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
