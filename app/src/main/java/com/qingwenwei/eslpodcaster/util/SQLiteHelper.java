package com.qingwenwei.eslpodcaster.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "eslpodcaster.db";



    private static final String SQL_CREATE_ITEMS =
            "CREATE TABLE books ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, "+
            "author TEXT )";

    private static final String SQL_DELETE_ITEMS =
            "DROP TABLE IF EXISTS " + "books";




    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("SQLiteHelper()");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        db.execSQL(SQL_CREATE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ITEMS);
        onCreate(db);
    }
}
