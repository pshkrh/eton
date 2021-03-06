package com.pshkrh.eton.Model;

import java.util.ArrayList;
import java.util.List;

public class Note {
    public String title,description,date;
    public int starred;

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void setStarred(int starred) {
        this.starred = starred;
    }

    public int getStarred() {

        return starred;
    }

    public Note(String title, String description, String date, int starred) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.starred = starred;

    }

    public Note(String title, String description, int starred) {
        this.title = title;
        this.description = description;
        this.starred = starred;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static ArrayList<Note> createNotesList(int n) {
        ArrayList<Note> notes = new ArrayList<Note>();

        for(int i=0; i<n;i++){
            notes.add(new Note("Title " + i, "Description " + i));
        }
        return notes;
    }
}
