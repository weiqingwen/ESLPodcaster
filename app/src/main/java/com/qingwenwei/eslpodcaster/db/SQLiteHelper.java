package com.qingwenwei.eslpodcaster.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
    //TAG
    private static final String TAG = "SQLiteHelper";

    //database name
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "eslpodcaster.db";

    //table creation query
    private static final String SQL_CREATE_EPISODES_TABLE =
                    "CREATE TABLE " +
                    DBConstants.EpisodeColumn.TABLE_EPISODES        + " ( " +
                    DBConstants.EpisodeColumn.KEY_ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DBConstants.EpisodeColumn.KEY_TITLE             + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_SUBTITLE          + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_CONTENT           + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_CATEGORY          + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_PUB_DATE          + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_AUDIO_URL         + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_WEB_URL           + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_ARCHIVED          + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_ARCHIVED_DATE     + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_LOCAL_AUDIO_FILE  + " TEXT, " +
                    DBConstants.EpisodeColumn.KEY_DOWNLOADED_DATE   + " TEXT )";

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(TAG,"constructor()");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EPISODES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DBConstants.EpisodeColumn.TABLE_EPISODES);
        onCreate(db);
    }
}
