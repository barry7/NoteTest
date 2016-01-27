package com.barry.notetest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * GreenDao
 * Thread  -->UI
 * EventBus
 * Created by Barry on 2016/1/15 0015.
 */
public class MyDataBase extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "notes";
    public static final String CONTENT = "content";
    public static final String PATH = "path";
    public static final String VIDEO = "video";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String TIME = "time";

    public MyDataBase(Context context) {
        super(context, "notes", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "("
                + ID + " integer primary key autoincrement,"
                + TITLE + " text not null,"
                + CONTENT + " text,"
                + PATH + " text,"
                + VIDEO + " text,"
                + TIME + " text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
