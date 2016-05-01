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

    // PodcastEpisode Table name
    private static final String TABLE_EPISODES = "episodes";

    // PodcastEpisodes Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_CONTENT= "content";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_PUB_DATE = "pub_date";
    private static final String KEY_AUDIO_URL = "audio_url";
    private static final String KEY_WEB_URL = "web_url";
    private static final String KEY_DOWNLOADED = "downloaded";
    private static final String KEY_FAVOURED = "favoured";

    //queries
    private static final String SQL_CREATE_ITEMS =
            "CREATE TABLE " + TABLE_EPISODES + " ( " +
            KEY_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE       + " TEXT, " +
            KEY_SUBTITLE    + " TEXT, " +
            KEY_CONTENT     + " TEXT, " +
            KEY_CATEGORY    + " TEXT, " +
            KEY_PUB_DATE    + " TEXT, " +
            KEY_AUDIO_URL   + " TEXT, " +
            KEY_WEB_URL     + " TEXT, " +
            KEY_DOWNLOADED  + " INTEGER, " +
            KEY_FAVOURED    + " INTEGER )";

    private static final String SQL_DELETE_ITEMS =
            "DROP TABLE IF EXISTS " + TABLE_EPISODES;

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

        // original episode
        PodcastEpisode originalEpisode = getEpisode(episode.getTitle());

        //episode already exists
        if(originalEpisode != null){
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

        if(episode.isDownloaded()){
            values.put(KEY_DOWNLOADED, 1);
        }else{
            values.put(KEY_DOWNLOADED, 0);
        }

        if(episode.isFavoured()){
            values.put(KEY_FAVOURED, 1);
        }else{
            values.put(KEY_FAVOURED, 0);
        }

        long newRowId; //new primary key
        newRowId = db.insert(TABLE_EPISODES, null, values);
        db.close();

        return newRowId;
    }

    public int updateEpisode(PodcastEpisode episode){
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

        if(episode.isDownloaded()){
            values.put(KEY_DOWNLOADED, 1);
        }else{
            values.put(KEY_DOWNLOADED, 0);
        }

        if(episode.isFavoured()){
            values.put(KEY_FAVOURED, 1);
        }else{
            values.put(KEY_FAVOURED, 0);
        }

        int i = db.update(TABLE_EPISODES, //table
                values, // column/value
                KEY_TITLE + " = ?", // selections
                new String[] {episode.getTitle()}); //selection args

        return i;
    }

    public boolean smartUpdate(PodcastEpisode newEpisode){
        long rowID = addEpisode(newEpisode);

        // error occurred when inserting a new row
        if(rowID == -1 || rowID < -2){
            return false;
        }

        // -2 means episode exist, update the original attributes
        if(rowID == -2){
            PodcastEpisode originalEpisode = getEpisode(newEpisode.getTitle());

            String title = originalEpisode.getTitle().equals(newEpisode.getTitle()) ?
                    originalEpisode.getTitle() : newEpisode.getTitle();

            String subtitle = originalEpisode.getSubtitle().equals(newEpisode.getSubtitle()) ?
                    originalEpisode.getSubtitle() : newEpisode.getSubtitle();

            String content = originalEpisode.getContent().equals(newEpisode.getContent()) ?
                    originalEpisode.getContent() : newEpisode.getContent();

            String category = originalEpisode.getCategory().equals(newEpisode.getCategory()) ?
                    originalEpisode.getCategory() : newEpisode.getCategory();

            String pubDate = originalEpisode.getPubDate().equals(newEpisode.getPubDate()) ?
                    originalEpisode.getPubDate() : newEpisode.getPubDate();

            String audioURL = originalEpisode.getAudioFileUrl().equals(newEpisode.getAudioFileUrl()) ?
                    originalEpisode.getAudioFileUrl() : newEpisode.getAudioFileUrl();

            String webURL = originalEpisode.getWebUrl().equals(newEpisode.getWebUrl()) ?
                    originalEpisode.getWebUrl() : newEpisode.getWebUrl();

            boolean isDownloaded = originalEpisode.isDownloaded() == (newEpisode.isDownloaded()) ?
                    originalEpisode.isDownloaded() : newEpisode.isDownloaded();

            boolean isFavoured = originalEpisode.isFavoured() == (newEpisode.isFavoured()) ?
                    originalEpisode.isFavoured() : newEpisode.isFavoured();

            // build a PodcastEpisode for updating
            PodcastEpisode episode = new PodcastEpisode(
                    title,
                    subtitle,
                    content,
                    pubDate,
                    audioURL,
                    webURL,
                    category,
                    isDownloaded,
                    isFavoured);

            updateEpisode(episode);
        }

        return true;
    }

    public PodcastEpisode getEpisode(String title){

        SQLiteDatabase db = this.getWritableDatabase();
        String[] projection = {KEY_ID,
                KEY_TITLE,
                KEY_SUBTITLE,
                KEY_CONTENT,
                KEY_CATEGORY,
                KEY_PUB_DATE,
                KEY_AUDIO_URL,
                KEY_WEB_URL,
                KEY_DOWNLOADED,
                KEY_FAVOURED};

        Cursor cursor = db.query(TABLE_EPISODES,projection,"title = ?",new String[]{title},null,null,null);

        if (cursor.getCount() != 0){
            cursor.moveToFirst();

            //build PodcastEpisode object
            PodcastEpisode episode = new PodcastEpisode();
            episode.setTitle(cursor.getString(1));
            episode.setSubtitle(cursor.getString(2));
            episode.setContent(cursor.getString(3));
            episode.setCategory(cursor.getString(4));
            episode.setPubDate(cursor.getString(5));
            episode.setAudioFileUrl(cursor.getString(6));
            episode.setWebUrl(cursor.getString(7));
            int downloaded = cursor.getInt(8);
            int favoured = cursor.getInt(9);

            if(downloaded == 1) {
                episode.setDownloaded(true);
            }else{
                episode.setDownloaded(false);
            }

            if(favoured == 1) {
                episode.setFavoured(true);
            }else{
                episode.setFavoured(false);
            }

            Log.i(TAG, "getEpisode() title:" + cursor.getString(1));

            db.close();
            return episode;
        }else{
            db.close();
            return null;
        }
    }

    public void deleteEpisode(PodcastEpisode episode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EPISODES,
                KEY_TITLE + " = ?",
                new String[] {episode.getTitle()});
        db.close();

        Log.i("deleteBook()", episode.getTitle());
    }

    public List<PodcastEpisode> getAllFavouredEpisodes(){
        List<PodcastEpisode> episodes = getAllEpisodes();
        List<PodcastEpisode> favouredEpisodes = new ArrayList<>();
        for(PodcastEpisode ep : episodes){
            if(ep.isFavoured()){
                favouredEpisodes.add(ep);
            }
        }
        Log.i(TAG,"getAllFavouredEpisodes() size:" + favouredEpisodes.size());
        return favouredEpisodes;
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
                episode.setContent(cursor.getString(3));
                episode.setCategory(cursor.getString(4));
                episode.setPubDate(cursor.getString(5));
                episode.setAudioFileUrl(cursor.getString(6));
                episode.setWebUrl(cursor.getString(7));
                int downloaded = cursor.getInt(8);
                int favoured = cursor.getInt(9);

                if(downloaded == 1) {
                    episode.setDownloaded(true);
                }else{
                    episode.setDownloaded(false);
                }

                if(favoured == 1) {
                    episode.setFavoured(true);
                }else{
                    episode.setFavoured(false);
                }

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
        db.execSQL("delete from "+ TABLE_EPISODES); //instead, db.delete(TABLE_EPISODES,null,null);
        Log.i(TAG,"deleteAllEpisodes()");
    }

}
