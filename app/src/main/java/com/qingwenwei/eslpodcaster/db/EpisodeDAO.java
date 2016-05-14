package com.qingwenwei.eslpodcaster.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import java.util.ArrayList;
import java.util.List;

public class EpisodeDAO {
    private static String TAG = "EpisodeDAO";

    private SQLiteHelper sqliteHelper;
    private SQLiteDatabase database;

    public EpisodeDAO(Context context){
        sqliteHelper = new SQLiteHelper(context.getApplicationContext());
    }

    public void open(){
        database = sqliteHelper.getWritableDatabase();
    }

    public void close(){
        sqliteHelper.close();
    }

    public boolean hasEpisode(PodcastEpisode episode){
        open();

        String query = "SELECT * FROM " +
                DBConstants.EpisodeColumn.TABLE_EPISODES + " WHERE " +
                DBConstants.EpisodeColumn.KEY_TITLE + " =?";

        Cursor cursor = database.rawQuery(query, new String[]{episode.getTitle()});
        boolean hasEpisode = false;
        if(cursor.moveToFirst()){
            hasEpisode = true;
        }

        cursor.close();
        close();
        return hasEpisode;
    }

    public long createEpisode(PodcastEpisode episode){

        //return -2 if the episode already exist
        if(hasEpisode(episode)){
            return -2;
        }

        open();

        //build episode entry
        ContentValues values = new ContentValues();
        values.put(DBConstants.EpisodeColumn.KEY_TITLE, episode.getTitle());
        values.put(DBConstants.EpisodeColumn.KEY_SUBTITLE, episode.getSubtitle());
        values.put(DBConstants.EpisodeColumn.KEY_CONTENT, episode.getContent());
        values.put(DBConstants.EpisodeColumn.KEY_CATEGORY, episode.getCategory());
        values.put(DBConstants.EpisodeColumn.KEY_PUB_DATE, episode.getPubDate());
        values.put(DBConstants.EpisodeColumn.KEY_AUDIO_URL, episode.getAudioFileUrl());
        values.put(DBConstants.EpisodeColumn.KEY_WEB_URL, episode.getWebUrl());
        values.put(DBConstants.EpisodeColumn.KEY_ARCHIVED, episode.getArchived());
        values.put(DBConstants.EpisodeColumn.KEY_LOCAL_AUDIO_FILE, episode.getLocalAudioFile());

        //new primary key
        long newRowId;
        newRowId = database.insert(DBConstants.EpisodeColumn.TABLE_EPISODES, null, values);
        close();

        Log.i(TAG, "addEpisode() " + "id: " + newRowId + "  " + episode.getTitle());
        return newRowId;
    }

    public PodcastEpisode getEpisode(PodcastEpisode episode){
        open();

        String[] projection = {
                DBConstants.EpisodeColumn.KEY_ID,
                DBConstants.EpisodeColumn.KEY_TITLE,
                DBConstants.EpisodeColumn.KEY_SUBTITLE,
                DBConstants.EpisodeColumn.KEY_CONTENT,
                DBConstants.EpisodeColumn.KEY_CATEGORY,
                DBConstants.EpisodeColumn.KEY_PUB_DATE,
                DBConstants.EpisodeColumn.KEY_AUDIO_URL,
                DBConstants.EpisodeColumn.KEY_WEB_URL,
                DBConstants.EpisodeColumn.KEY_ARCHIVED,
                DBConstants.EpisodeColumn.KEY_LOCAL_AUDIO_FILE
        };

        Cursor cursor = database.query(DBConstants.EpisodeColumn.TABLE_EPISODES,projection,"title = ?",new String[]{episode.getTitle()},null,null,null);

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
            ep.setArchived(cursor.getString(8));
            ep.setLocalAudioFile(cursor.getString(9));

            cursor.close();
            close();

            Log.i(TAG, "getEpisode() title:" + cursor.getString(1));
            return ep;
        }else{
            cursor.close();
            close();
            return null;
        }
    }

    public int updateEpisode(PodcastEpisode episode){
        open();

        //build episode entry
        ContentValues values = new ContentValues();
        values.put(DBConstants.EpisodeColumn.KEY_TITLE, episode.getTitle());
        values.put(DBConstants.EpisodeColumn.KEY_SUBTITLE, episode.getSubtitle());
        values.put(DBConstants.EpisodeColumn.KEY_CONTENT, episode.getContent());
        values.put(DBConstants.EpisodeColumn.KEY_CATEGORY, episode.getCategory());
        values.put(DBConstants.EpisodeColumn.KEY_PUB_DATE, episode.getPubDate());
        values.put(DBConstants.EpisodeColumn.KEY_AUDIO_URL, episode.getAudioFileUrl());
        values.put(DBConstants.EpisodeColumn.KEY_WEB_URL, episode.getWebUrl());
        values.put(DBConstants.EpisodeColumn.KEY_ARCHIVED, episode.getArchived());
        values.put(DBConstants.EpisodeColumn.KEY_LOCAL_AUDIO_FILE, episode.getLocalAudioFile());

        int i = database.update(
                DBConstants.EpisodeColumn.TABLE_EPISODES, //table
                values, // column/value
                DBConstants.EpisodeColumn.KEY_TITLE + " = ?", // selections
                new String[] {episode.getTitle()}); //selection args
        close();;

        Log.i("updateEpisode() ", episode.getTitle());
        return i;
    }

    public void deleteEpisode(PodcastEpisode episode){
        open();

        database.delete(
                DBConstants.EpisodeColumn.TABLE_EPISODES,
                DBConstants.EpisodeColumn.KEY_TITLE + " = ?",
                new String[] {episode.getTitle()}
        );
        close();

        Log.i("deleteEpisode() ", episode.getTitle());
    }

    public List<PodcastEpisode> getAllEpisodes(){
        open();

        List<PodcastEpisode> episodes = new ArrayList<>();

        //build the query
        String query = "SELECT * FROM " +
                DBConstants.EpisodeColumn.TABLE_EPISODES + " ORDER BY " +
                DBConstants.EpisodeColumn.KEY_ID + " DESC";

        Cursor cursor = database.rawQuery(query, null);

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
                episode.setArchived(cursor.getString(8));
                episode.setLocalAudioFile(cursor.getString(9));

                // add episode to list
                episodes.add(episode);
            } while (cursor.moveToNext());
        }

        cursor.close();
        close();

        Log.i(TAG,"getAllEpisodes() size:" + episodes.size());
        for(PodcastEpisode ep : episodes){
            Log.i(TAG,"---------> " + ep.getTitle());
        }
        return episodes;
    }

    public void deleteAllEpisodes(){
        open();
        database.execSQL("delete from "+ DBConstants.EpisodeColumn.TABLE_EPISODES); //instead, db.delete(TABLE_EPISODES,null,null);
        close();
        Log.i(TAG,"deleteAllEpisodes()");
    }

    public List<PodcastEpisode> getAllArchivedEpisodes(){
        List<PodcastEpisode> allEpisodes = getAllEpisodes();
        List<PodcastEpisode> archivedEpisodes = new ArrayList<>();

        for(PodcastEpisode ep : allEpisodes){
            String archived = ep.getArchived();
            if (archived != null && !archived.equals("")){
                archivedEpisodes.add(ep);
            }
        }

        Log.i(TAG, "getAllArchivedEpisodes() size: " + archivedEpisodes.size());
        return archivedEpisodes;
    }

    public List<PodcastEpisode> getAllDownloadedEpisodes(){
        List<PodcastEpisode> allEpisodes = getAllEpisodes();
        List<PodcastEpisode> downloadedEpisodes = new ArrayList<>();

        for(PodcastEpisode ep : allEpisodes){
            String localFile = ep.getLocalAudioFile();
            if (localFile != null && !localFile.equals("")){
                downloadedEpisodes.add(ep);
            }
        }

        Log.i(TAG, "getAllDownloadedEpisodes() size: " + downloadedEpisodes.size());
        return downloadedEpisodes;
    }

}
