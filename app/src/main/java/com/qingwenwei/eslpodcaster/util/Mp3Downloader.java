package com.qingwenwei.eslpodcaster.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Mp3Downloader {
    private static final String TAG = "Mp3Downloader";

    private Context context;
    private PodcastEpisode playingEpisode;

    // Constructor
    public Mp3Downloader(Context context, PodcastEpisode playingEpisode){
        this.context = context;
        this.playingEpisode = playingEpisode;
    }

    public void startDownload(){
        String audioUrl = playingEpisode.getAudioFileUrl();
        Toast.makeText(context,"Downloading " + playingEpisode.getTitle(), Toast.LENGTH_SHORT).show();
        new DownloadFileAsyncTask().execute(audioUrl);
    }

    class DownloadFileAsyncTask extends AsyncTask<String, Integer, String> {
        String fileName;

        @Override
        protected String doInBackground(String... urlStr) {
            try {
                URL url = new URL(urlStr[0]);
                fileName = FileUtil.guessFileName(urlStr[0]);
                String targetAudioPath =  FileUtil.getAudioRootDir() + "/" + fileName;
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
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            Log.i(TAG, fileName + " downloading... " + progress[0] + "%");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // modify original states of the episode
            String playingTitle = playingEpisode.getTitle();
            SQLiteHelper db = new SQLiteHelper(context);
            PodcastEpisode originalEpisode = db.getEpisode(playingTitle);
            boolean result;
            if(originalEpisode != null) {
                originalEpisode.setDownloaded(true);
                result = db.smartUpdate(originalEpisode);
            }else{
                playingEpisode.setDownloaded(true);
                result = db.smartUpdate(playingEpisode);
            }

            db.close();
            Log.i(TAG, "Finished downloading " + playingTitle + " smartUpdate() " + result);
            Toast.makeText(context,"Finished downloading " + playingTitle, Toast.LENGTH_LONG).show();
        }
    }
}
