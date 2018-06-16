package com.pshkrh.eton.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pshkrh.eton.Model.Note;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelper";

    public static final String TABLE_NAME = "notes";
    public static final String TABLE_NAME_TWO = "bin";
    public static final String COL0 = "ID";
    public static final String COL1 = "Title";
    public static final String COL2 = "Description";
    public static final String COL3 = "Date";
    public static final String COL4 = "Star";

    Date currentTime = Calendar.getInstance().getTime();


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable1 = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL1 + " TEXT, " + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " DATETIME)";

        String createTable2 = "CREATE TABLE " + TABLE_NAME_TWO + " (ID INTEGER PRIMARY KEY, " +
                COL1 + " TEXT, " + COL2 + " TEXT, " + COL3 + " TEXT, " + COL4 + " DATETIME)";

        sqLiteDatabase.execSQL(createTable1);
        sqLiteDatabase.execSQL(createTable2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(Note note, int reAdd){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String noteTitle = note.getTitle();
        String noteDescription = note.getDescription();
        String noteDate="";
        if(reAdd == 0){
            noteDate = StringHelper.getDateTime();
            Log.d(TAG, "reAdd 0: Added timestamp from clock");
        }
        else if(reAdd == 1){
            noteDate = note.getDate();
            Log.d(TAG, "reAdd 1: Added timestamp from object");
        }
        int starred = note.getStarred();
        contentValues.put(COL1,noteTitle);
        contentValues.put(COL2,noteDescription);
        contentValues.put(COL3,noteDate);
        contentValues.put(COL4,starred);

        Log.d(TAG, "addData: Adding note with title " + noteTitle + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null,contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean addDeletedData(Note note){
        int id=0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String noteTitle = note.getTitle();
        String noteDescription = note.getDescription();
        String noteDate = note.getDate();
        int starred = note.getStarred();

        Cursor data = getItemID(note.getDate());
        while(data.moveToNext()){
            id = data.getInt(0);
        }

        contentValues.put(COL0,id);
        contentValues.put(COL1,noteTitle);
        contentValues.put(COL2,noteDescription);
        contentValues.put(COL3,noteDate);
        contentValues.put(COL4,starred);

        Log.d(TAG, "addDeletedData: Adding deleted note with title " + noteTitle + "to " + TABLE_NAME_TWO);

        long result = db.insert(TABLE_NAME_TWO, null,contentValues);

        if(result == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL3 + " DESC";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getStarredData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL4 + " = '" + "1'" + " ORDER BY " + COL3 + " DESC";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getDeletedData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_TWO + " ORDER BY " + COL3 + " DESC";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getItemID(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL0 + " FROM " + TABLE_NAME + " WHERE " + COL3 + " = '" + date + "'";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getSortedData(String choice){
        SQLiteDatabase db = this.getWritableDatabase();
        String query="";
        switch(choice){
            case "alphabetical":
                query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL1 + " ASC";
                break;

            case "ascending":
                query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL3 + " ASC";
                break;

            case "descending":
                query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL3 + " DESC";
                break;
        }
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public void updateNote(String title, String description, int id, int starred){
        SQLiteDatabase db = this.getWritableDatabase();
        String newTitle = StringHelper.fixQuery(title);
        String newDescription = StringHelper.fixQuery(description);

        String query = "UPDATE " + TABLE_NAME + " SET " + COL1 + " = '" + newTitle + "', " + COL2 + " = '" + newDescription +
                "', " + COL4 + " = '" + starred + "' WHERE " + COL0 + " = '" + id + "'";

        Log.d(TAG, "updateNote: Query = " + query);
        Log.d(TAG, "updateNote: Setting title to " + title);
        Log.d(TAG, "updateNote: Setting description to " + description);

        db.execSQL(query);
    }

    public void deleteNote(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL0 + " = '" + id + "'";

        Log.d(TAG, "deleteNote: Query = " + query);
        Log.d(TAG, "deleteNote: Deleteting Note with ID = " + id);

        db.execSQL(query);
    }

    public void deleteBinNote(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_TWO + " WHERE " + COL0 + " = '" + id + "'";

        Log.d(TAG, "deleteNote: Query = " + query);
        Log.d(TAG, "deleteNote: Deleting Note with ID = " + id);

        db.execSQL(query);
    }

    public Cursor getDeletedItemID(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL0 + " FROM " + TABLE_NAME_TWO + " WHERE " + COL3 + " = '" + date + "'";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME_TWO;

        Log.d(TAG, "deleteAll: Deleting All Notes from Bin");

        db.execSQL(query);
    }

    public Cursor getSearchedData(String keyword){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + " LIKE '%"
        + keyword + "%' OR " + COL2 + " LIKE '%" + keyword + "%'" + " ORDER BY " + COL3 + " DESC";
        Log.d(TAG, "Searched Data Query = " + query);
        Cursor data = db.rawQuery(query,null);
        return data;
    }

}
