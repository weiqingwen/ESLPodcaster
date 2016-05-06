package com.qingwenwei.eslpodcaster.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDatabaseManager extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteDatabaseManager";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "eslpodcaster.db";

    //tables name
    private static final String TABLE_FAVORITES = "FAVORITES";
    private static final String TABLE_DOWNLOADS = "DOWNLOADS";

    //table columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_CONTENT= "content";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_PUB_DATE = "pub_date";
    private static final String KEY_AUDIO_URL = "audio_url";
    private static final String KEY_WEB_URL = "web_url";

    //DOWNLOADS table column name
    private static final String KEY_LOCAL_AUDIO_FILE = "local_audio_file";

    //queries
    private static final String SQL_CREATE_FAVORITES_TABLE =
            "CREATE TABLE " + TABLE_FAVORITES + " ( " +
                    KEY_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TITLE       + " TEXT, " +
                    KEY_SUBTITLE    + " TEXT, " +
                    KEY_CONTENT     + " TEXT, " +
                    KEY_CATEGORY    + " TEXT, " +
                    KEY_PUB_DATE    + " TEXT, " +
                    KEY_AUDIO_URL   + " TEXT, " +
                    KEY_WEB_URL     + " TEXT )";

    //queries
    private static final String SQL_CREATE_DOWNLOADS_TABLE =
            "CREATE TABLE " + TABLE_DOWNLOADS + " ( " +
                    KEY_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TITLE       + " TEXT, " +
                    KEY_SUBTITLE    + " TEXT, " +
                    KEY_CONTENT     + " TEXT, " +
                    KEY_CATEGORY    + " TEXT, " +
                    KEY_PUB_DATE    + " TEXT, " +
                    KEY_AUDIO_URL   + " TEXT, " +
                    KEY_WEB_URL     + " TEXT, " +
                    KEY_LOCAL_AUDIO_FILE + " TEXT )";


    private static SQLiteDatabaseManager instance;

    public static SQLiteDatabaseManager getInstance(Context context){
        if(instance == null){
            instance = new SQLiteDatabaseManager(context);
        }
        return instance;
    }

    public SQLiteDatabaseManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(TAG,"constructor()");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
        db.execSQL(SQL_CREATE_DOWNLOADS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADS);

        // create new tables
        onCreate(db);
    }

    /*
        FAVORITES table CRUD operations
     */
    public boolean hasFavoriteEpisode(PodcastEpisode episode){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_TITLE + " =?";

        Cursor cursor = db.rawQuery(query, new String[]{episode.getTitle()});
        boolean hasEpisode = false;
        if(cursor.moveToFirst()){
            hasEpisode = true;
        }
        cursor.close();
        db.close();
        return hasEpisode;
    }

    public long addFavoriteEpisode(PodcastEpisode episode){
        if(hasFavoriteEpisode(episode)){
            Log.i(TAG, "addFavoriteEpisode() already exists " + episode.getTitle());
            return -2;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        //build episode entry
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, episode.getTitle());
        values.put(KEY_SUBTITLE, episode.getSubtitle());
        values.put(KEY_CONTENT, episode.getContent());
        values.put(KEY_CATEGORY, episode.getCategory());
        values.put(KEY_PUB_DATE, episode.getPubDate());
        values.put(KEY_AUDIO_URL, episode.getAudioFileUrl());
        values.put(KEY_WEB_URL, episode.getWebUrl());

        long newRowId; //new primary key
        newRowId = db.insert(TABLE_FAVORITES, null, values);
        db.close();

        Log.i(TAG, "addFavoriteEpisode() " + "id:" + newRowId + "  " + episode.getTitle());
        return newRowId;
    }

    public PodcastEpisode getFavoriteEpisode(PodcastEpisode episode){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {KEY_ID,
                KEY_TITLE,
                KEY_SUBTITLE,
                KEY_CONTENT,
                KEY_CATEGORY,
                KEY_PUB_DATE,
                KEY_AUDIO_URL,
                KEY_WEB_URL};

        Cursor cursor = db.query(TABLE_FAVORITES,projection,"title = ?",new String[]{episode.getTitle()},null,null,null);

        if (cursor.getCount() != 0){
            cursor.moveToFirst();

            //build PodcastEpisode object
            PodcastEpisode ep = new PodcastEpisode();
            ep.setTitle(cursor.getString(1));
            ep.setSubtitle(cursor.getString(2));
            ep.setContent(cursor.getString(3));
            ep.setCategory(cursor.getString(4));
            ep.setPubDate(cursor.getString(5));
            ep.setAudioFileUrl(cursor.getString(6));
            ep.setWebUrl(cursor.getString(7));

            Log.i(TAG, "getFavoriteEpisode() title:" + cursor.getString(1));

            cursor.close();
            db.close();
            return ep;
        }else{
            cursor.close();
            db.close();
            return null;
        }
    }

    public List<PodcastEpisode> getAllFavoriteEpisodes(){
        List<PodcastEpisode> episodes = new ArrayList<>();

        //build the query
        String query = "SELECT * FROM " + TABLE_FAVORITES + " ORDER BY " + KEY_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                PodcastEpisode episode = new PodcastEpisode();
                episode.setTitle(cursor.getString(1));
                episode.setSubtitle(cursor.getString(2));
                episode.setContent(cursor.getString(3));
                episode.setCategory(cursor.getString(4));
                episode.setPubDate(cursor.getString(5));
                episode.setAudioFileUrl(cursor.getString(6));
                episode.setWebUrl(cursor.getString(7));

                // add episode to list
                episodes.add(episode);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.i(TAG,"getAllFavoriteEpisodes() size:" + episodes.size());
        for(PodcastEpisode ep : episodes){
            Log.i(TAG,"---------> " + ep.getTitle());
        }

        return episodes;
    }

    public int updateFavoriteEpisode(PodcastEpisode episode){
        SQLiteDatabase db = this.getWritableDatabase();

        //build episode entry
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, episode.getTitle());
        values.put(KEY_SUBTITLE, episode.getSubtitle());
        values.put(KEY_CONTENT, episode.getContent());
        values.put(KEY_CATEGORY, episode.getCategory());
        values.put(KEY_PUB_DATE, episode.getPubDate());
        values.put(KEY_AUDIO_URL, episode.getAudioFileUrl());
        values.put(KEY_WEB_URL, episode.getWebUrl());

        int i = db.update(TABLE_FAVORITES, //table
                values, // column/value
                KEY_TITLE + " = ?", // selections
                new String[] {episode.getTitle()}); //selection args
        db.close();

        Log.i("updateFavoriteEpisode()", episode.getTitle());
        return i;
    }

    public void deleteFavoriteEpisode(PodcastEpisode episode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES,
                KEY_TITLE + " = ?",
                new String[] {episode.getTitle()});
        db.close();

        Log.i("deleteFavoriteEpisode()", episode.getTitle());
    }

    public void deleteAllFavoriteEpisodes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_FAVORITES); //instead, db.delete(TABLE_FAVORITES,null,null);
        db.close();

        Log.i(TAG,"deleteAllFavoriteEpisodes()");
    }


    /*
        DOWNLOADS table CRUD operations
     */
    public boolean hasDownloadEpisode(PodcastEpisode episode){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_DOWNLOADS + " WHERE " + KEY_TITLE + " =?";

        Cursor cursor = db.rawQuery(query, new String[]{episode.getTitle()});
        boolean hasEpisode = false;
        if(cursor.moveToFirst()){
            hasEpisode = true;
        }
        cursor.close();
        db.close();
        return hasEpisode;
    }

    public long addDownloadEpisode(PodcastEpisode episode){
        if(hasDownloadEpisode(episode)){
            return -2;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        //build episode entry
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, episode.getTitle());
        values.put(KEY_SUBTITLE, episode.getSubtitle());
        values.put(KEY_CONTENT, episode.getContent());
        values.put(KEY_CATEGORY, episode.getCategory());
        values.put(KEY_PUB_DATE, episode.getPubDate());
        values.put(KEY_AUDIO_URL, episode.getAudioFileUrl());
        values.put(KEY_WEB_URL, episode.getWebUrl());
        values.put(KEY_LOCAL_AUDIO_FILE, episode.getLocalAudioFile());

        long newRowId; //new primary key
        newRowId = db.insert(TABLE_DOWNLOADS, null, values);
        db.close();

        Log.i(TAG, "addDownloadEpisode() " + "id:" + newRowId + "  " + episode.getTitle());
        return newRowId;
    }

    public PodcastEpisode getDownloadEpisode(PodcastEpisode episode){
        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {
                KEY_ID,
                KEY_TITLE,
                KEY_SUBTITLE,
                KEY_CONTENT,
                KEY_CATEGORY,
                KEY_PUB_DATE,
                KEY_AUDIO_URL,
                KEY_WEB_URL,
                KEY_LOCAL_AUDIO_FILE
        };

        Cursor cursor = db.query(TABLE_DOWNLOADS,projection,"title = ?",new String[]{episode.getTitle()},null,null,null);

        if (cursor.getCount() != 0){
            cursor.moveToFirst();

            //build PodcastEpisode object
            PodcastEpisode ep = new PodcastEpisode();
            ep.setTitle(cursor.getString(1));
            ep.setSubtitle(cursor.getString(2));
            ep.setContent(cursor.getString(3));
            ep.setCategory(cursor.getString(4));
            ep.setPubDate(cursor.getString(5));
            ep.setAudioFileUrl(cursor.getString(6));
            ep.setWebUrl(cursor.getString(7));
            ep.setLocalAudioFile(cursor.getString(8));

            Log.i(TAG, "getDownloadEpisode() title:" + cursor.getString(1));

            cursor.close();
            db.close();
            return ep;
        }else{
            cursor.close();
            db.close();
            return null;
        }
    }

    public List<PodcastEpisode> getAllDownloadEpisodes(){
        List<PodcastEpisode> episodes = new ArrayList<>();

        //build the query
        String query = "SELECT * FROM " + TABLE_DOWNLOADS + " ORDER BY " + KEY_ID + " DESC";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                PodcastEpisode episode = new PodcastEpisode();
                episode.setTitle(cursor.getString(1));
                episode.setSubtitle(cursor.getString(2));
                episode.setContent(cursor.getString(3));
                episode.setCategory(cursor.getString(4));
                episode.setPubDate(cursor.getString(5));
                episode.setAudioFileUrl(cursor.getString(6));
                episode.setWebUrl(cursor.getString(7));
                episode.setLocalAudioFile(cursor.getString(8));

                // add episode to list
                episodes.add(episode);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.i(TAG,"getAllDownloadEpisodes() size:" + episodes.size());
        for(PodcastEpisode ep : episodes){
            Log.i(TAG,"---------> " + ep.getTitle());
        }

        return episodes;
    }

    public int updateDownloadEpisode(PodcastEpisode episode){
        SQLiteDatabase db = this.getWritableDatabase();

        //build episode entry
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, episode.getTitle());
        values.put(KEY_SUBTITLE, episode.getSubtitle());
        values.put(KEY_CONTENT, episode.getContent());
        values.put(KEY_CATEGORY, episode.getCategory());
        values.put(KEY_PUB_DATE, episode.getPubDate());
        values.put(KEY_AUDIO_URL, episode.getAudioFileUrl());
        values.put(KEY_WEB_URL, episode.getWebUrl());
        values.put(KEY_LOCAL_AUDIO_FILE, episode.getLocalAudioFile());

        int i = db.update(TABLE_DOWNLOADS, //table
                values, // column/value
                KEY_TITLE + " = ?", // selections
                new String[] {episode.getTitle()}); //selection args
        db.close();

        Log.i("updateDownloadEpisode()", episode.getTitle());
        return i;
    }

    public void deleteDownloadEpisode(PodcastEpisode episode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOWNLOADS,
                KEY_TITLE + " = ?",
                new String[] {episode.getTitle()});
        db.close();

        Log.i("deleteDownloadEpisode()", episode.getTitle());
    }

    public void deleteAllDownloadEpisodes(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_DOWNLOADS); //instead, db.delete(TABLE_FAVORITES,null,null);
        db.close();

        Log.i(TAG,"deleteAllDownloadEpisodes()");
    }
}
