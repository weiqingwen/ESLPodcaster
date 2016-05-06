package com.qingwenwei.eslpodcaster.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.sqlite.SQLiteDatabaseManager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class EpisodeDownloadManager {
    private static final String TAG = "EpisodeDownloadManager";

    private Context context;

    //constructor
    public EpisodeDownloadManager(Context context){
        this.context = context;
    }

    public void downloadEpisode(PodcastEpisode episode){
        new DownloadEpisodeAsyncTask(episode).execute();
    }

    public void deleteEpisode(PodcastEpisode episode){

        //code to delete the local audio file ...

        changeEpisodeDownloadStatus(context,episode,false);

        //To delete the episode audio file
        //need to store the file path in database
        //...
    }

    private class DownloadEpisodeAsyncTask extends AsyncTask<Void, Integer, String> {
        private String fileName;
        private PodcastEpisode episode;

        public DownloadEpisodeAsyncTask(PodcastEpisode episode){
            this.episode = episode;
        }

        @Override
        protected String doInBackground(Void... params) {
            String targetAudioPath = null;
            try {
                String audioUrl = episode.getAudioFileUrl();
                URL url = new URL(audioUrl);
                fileName = FileUtil.guessFileName(audioUrl);
                targetAudioPath =  FileUtil.getAudioRootDir() + "/" + fileName;
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(targetAudioPath);
                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int)((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return targetAudioPath;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            Log.i(TAG, fileName + " downloading... " + progress[0] + "%");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(TAG, "downloaded at: " + s);
            episode.setLocalAudioFile(s);
            changeEpisodeDownloadStatus(context,episode,true);
        }
    }

    private void changeEpisodeDownloadStatus(Context context, PodcastEpisode episode, boolean downloaded){
        String title = episode.getTitle();
        SQLiteDatabaseManager db = new SQLiteDatabaseManager(context);
        if(downloaded){
            db.addDownloadEpisode(episode);
            Toast.makeText(context, "Downloaded " + title, Toast.LENGTH_LONG).show();
            Log.i(TAG, "Downloaded changeEpisodeDownloadStatus() " + title);

        }else{ // to delete the audio file
            String file = episode.getLocalAudioFile();
            if(FileUtil.deleteFile(file)){
                db.deleteDownloadEpisode(episode);
                Toast.makeText(context, "Deleted " + title, Toast.LENGTH_LONG).show();
                Log.i(TAG, "changeEpisodeDownloadStatus() Deleted " + title);
            }else{
                Toast.makeText(context, "Failed to delete " + title, Toast.LENGTH_LONG).show();
                Log.i(TAG, "changeEpisodeDownloadStatus() Failed to delete " + title);
            }

        }
        db.close();
    }
}
