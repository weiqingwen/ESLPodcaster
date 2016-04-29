package com.qingwenwei.eslpodcaster.util;

import android.os.AsyncTask;
import android.util.Log;

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

    public void startDownload(PodcastEpisode episode){
        String audioUrl = episode.getAudioFileUrl();
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
        }
    }
}
