package com.pshkrh.notes.Model;

import java.util.ArrayList;
import java.util.List;

public class Note {
    public String title,description,date;

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Note(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
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
