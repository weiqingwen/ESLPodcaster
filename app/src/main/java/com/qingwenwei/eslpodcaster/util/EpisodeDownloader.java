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

public class EpisodeDownloader {
    private static final String TAG = "EpisodeDownloader";

    private Context context;

    //constructor
    public EpisodeDownloader(Context context) {
        this.context = context;
    }

    public void startDownload(PodcastEpisode episode) {
        //check if episode already downloaded
        EpisodeDAO dao = new EpisodeDAO(context);
        if (dao.isDownloaded(episode)) {
            // if episode is downloaded
            Toast.makeText(context,"Already downloaded", Toast.LENGTH_LONG).show();
            dao.close();
            return;
        }

        //running in parallel with any other potential running AsyncTasks
        Toast.makeText(context,"Downloading in the background......", Toast.LENGTH_LONG).show();
        new DownloadEpisodeAsyncTask(episode).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    //store or change episode status in the database
    private void postDownload(Context context, PodcastEpisode episode) {
        String localFile = episode.getLocalAudioFile();
        String downloadedDate = episode.getDownloadedDate();

        EpisodeDAO dao = new EpisodeDAO(context);
        if (dao.isArchived(episode)) {
            PodcastEpisode oldEpisode = dao.getEpisode(episode);
            oldEpisode.setLocalAudioFile(localFile);
            oldEpisode.setDownloadedDate(downloadedDate);
            dao.updateEpisode(oldEpisode);
        } else {
            dao.createEpisode(episode);
        }

        Toast.makeText(context, "Downloaded at " + localFile, Toast.LENGTH_LONG).show();
        Log.i(TAG, "downloaded at: " + localFile);
    }

    private class DownloadEpisodeAsyncTask extends AsyncTask<Void, Integer, String> {
        private String fileName;
        private PodcastEpisode episode;

        public DownloadEpisodeAsyncTask(PodcastEpisode episode) {
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

            //setup local audio file location and downloaded date
            episode.setLocalAudioFile(localFile);
            episode.setDownloadedDate(PodcastEpisode.currentDateString());
            postDownload(context,episode);
        }
    }
}
