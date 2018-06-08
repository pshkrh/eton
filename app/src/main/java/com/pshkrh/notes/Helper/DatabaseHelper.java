package com.pshkrh.notes.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pshkrh.notes.Model.Note;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    public static final String TABLE_NAME = "notes";
    public static final String COL0 = "ID";
    public static final String COL1 = "Title";
    public static final String COL2 = "Description";
    public static final String COL3 = "Date";

    Date currentTime = Calendar.getInstance().getTime();


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " + COL2 + " TEXT, " + COL3 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(Note note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String noteTitle = note.getTitle();
        String noteDescription = note.getDescription();
        String noteDate = currentTime.toString();
        contentValues.put(COL1,noteTitle);
        contentValues.put(COL2,noteDescription);
        contentValues.put(COL3,noteDate);

        Log.d(TAG, "addData: Adding note with title " + noteTitle + "to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null,contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query,null);
        return data;
    }


}
