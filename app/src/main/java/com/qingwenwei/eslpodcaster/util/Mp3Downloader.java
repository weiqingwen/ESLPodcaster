package com.qingwenwei.eslpodcaster.util;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.qingwenwei.eslpodcaster.constant.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Mp3Downloader {
    private static final String TAG = "Mp3Downloader";

    public void startDownload(){
        new DownloadFileAsyncTask().execute("http://libsyn.com/media/eslpod/ESLPod1203.mp3");
    }

    class DownloadFileAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urlStr) {
            try {
                URL url = new URL(urlStr[0]);
                String fileName = guessFileName(urlStr[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(getAppRootDir() + "/" + fileName);
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
            Log.i(TAG,"downloading... " + progress[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    //helper
    private String getAppRootDir(){
        String targetDir = getExternalStoragePath() + Constants.ROOT_DIRECTORY_NAME;

        if(directoryExist(targetDir)){
          return targetDir;
        }

        //if app root dir does not exist, create new root dir
        File file = new File(targetDir);
        boolean created = file.mkdir();
        if(created){
            return targetDir;
        }
        return null;
    }

    //helper
    private String guessFileName(String url){
        String fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(url);
        String fileName = URLUtil.guessFileName(url, null, fileExtenstion);
        Log.i(TAG,"guessFileName() " + fileName);
        return fileName;
    }

    //helper
    private String getExternalStoragePath(){
        String separator = "/";
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + separator; //instead, use getPath()
        Log.i(TAG,"external storage:" + sdcardPath);
        return sdcardPath;
    }

    //helper
    private boolean directoryExist(String dir){
        File file = new File(dir);
        if(file.exists() && file.isDirectory()){
            Log.i(TAG,"root dir exists.");
            return true;
        }
        Log.i(TAG, "root dir does not exist.");
        return false;
    }
}
