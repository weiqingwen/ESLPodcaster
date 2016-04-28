package com.qingwenwei.eslpodcaster.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper{
    private static final String TAG = "SQLiteHelper";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "eslpodcaster.db";

    private static final String SQL_CREATE_ITEMS =
            "CREATE TABLE episodes ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, "+
            "subtitle TEXT )";

    private static final String SQL_DELETE_ITEMS =
            "DROP TABLE IF EXISTS " + "episodes";

    // Books table name
    private static final String TABLE_EPISODES = "episodes";
    private static final String TABLE_DOWNLOADS = "downloads";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";


    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(TAG,"SQLiteHelper constructor()");
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

    public long addEpisode(PodcastEpisode episode){
        Log.i(TAG, "addEpisode() " + episode.getTitle());

        //episode already exists
        if(getEpisode(episode.getTitle()) != null){
            return -1;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, episode.getTitle()); // get title
        values.put(KEY_SUBTITLE, episode.getSubtitle()); // get subtitle

        long newRowId; //new primary key
        newRowId = db.insert(TABLE_EPISODES, null, values);

        db.close();
        return newRowId;
    }

    public PodcastEpisode getEpisode(String title){

        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {KEY_ID,KEY_TITLE,KEY_SUBTITLE};
        Cursor cursor = db.query(TABLE_EPISODES,projection,"title = ?",new String[] { String.valueOf(title) },null,null,null);

        if (cursor.getCount() != 0){
            cursor.moveToFirst();

            //build PodcastEpisode object
            PodcastEpisode episode = new PodcastEpisode();
            episode.setTitle(cursor.getString(1));
            episode.setSubtitle(cursor.getString(2));

            Log.i(TAG, "getEpisode() title:" + cursor.getString(1) + " subtitle:" + cursor.getString(2));

            return episode;
        }else{
            return null;
        }
    }

    public void deletEpisode(PodcastEpisode episode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EPISODES,
                KEY_TITLE + " = ?",
                new String[] { String.valueOf(episode.getTitle()) });
        db.close();

        Log.d("deleteBook()", episode.getTitle());
    }

    public List<PodcastEpisode> getAllEpisodes(){
        List<PodcastEpisode> episodes = new ArrayList<>();

        //build the query
        String query = "SELECT * FROM " + TABLE_EPISODES + " ORDER BY " + KEY_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        PodcastEpisode episode = null;
        if (cursor.moveToFirst()) {
            do {
                episode = new PodcastEpisode();
                episode.setTitle(cursor.getString(1));
                episode.setSubtitle(cursor.getString(2));

                // add episode to list
                episodes.add(episode);
            } while (cursor.moveToNext());
        }

        Log.i(TAG,"getAllEpisodes() size:" + episodes.size());

        for(PodcastEpisode ep : episodes){
            Log.i(TAG," @episode => " + ep.getTitle());
        }

        return episodes;
    }

    public void deleteAllEpisodes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_EPISODES);
//        db.delete(TABLE_EPISODES,null,null);
        Log.i(TAG,"deleteAllEpisodes()");
    }

}
