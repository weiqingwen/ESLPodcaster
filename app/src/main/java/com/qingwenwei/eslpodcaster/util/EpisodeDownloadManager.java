package com.qingwenwei.eslpodcaster.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.qingwenwei.eslpodcaster.db.EpisodeDAO;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

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

    public void startDownload(PodcastEpisode episode){

        //check if episode already downloaded
        EpisodeDAO dao = new EpisodeDAO(context);
        if (dao.hasEpisode(episode)){ // if episode is downloaded
            Toast.makeText(context,"Already downloaded", Toast.LENGTH_LONG).show();
            dao.close();
            return;
        }

        //running in parallel with any other potential running AsyncTasks
        Toast.makeText(context,"Downloading in the background......", Toast.LENGTH_LONG).show();
        new DownloadEpisodeAsyncTask(episode).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void deleteEpisode(PodcastEpisode episode){

        //delete the local audio file and ...
        //if the episode is archived, only remove the local file column string
        //otherwise, delete the episode entry in the database as well

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
        protected void onPostExecute(String localFile) {
            super.onPostExecute(localFile);
            episode.setLocalAudioFile(localFile);
            postDownload(context,episode);
        }
    }

    private void postDownload(Context context, PodcastEpisode episode){
        String localFile = episode.getLocalAudioFile();

        EpisodeDAO dao = new EpisodeDAO(context);
        if (dao.hasEpisode(episode)){
            dao.updateEpisode(episode);
        }else{
            dao.createEpisode(episode);
        }
        dao.close();

        Toast.makeText(context, "Downloaded at " + localFile, Toast.LENGTH_LONG).show();
        Log.i(TAG, "downloaded at: " + localFile);
    }
}
