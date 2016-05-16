package com.qingwenwei.eslpodcaster.util;

import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import com.qingwenwei.eslpodcaster.constant.Constants;

import java.io.File;

public class FileUtil {

    public static String getAppRootDir(){
        String rootDir = getExternalStoragePath() + Constants.ROOT_DIRECTORY_NAME;

        if(directoryExist(rootDir)){
            System.out.println("FileUtil: getAppRootDir() " + rootDir);
            return rootDir;
        }

        //if app root dir does not exist, create new root dir
        File file = new File(rootDir);
        boolean created = file.mkdir();
        if(created){
            System.out.println("FileUtil: getAppRootDir() " + rootDir);
            return rootDir;
        }
        return null;
    }

    public static String getAudioRootDir(){
        String rootDir = getAppRootDir();
        if(rootDir != null){
            String audioDir = rootDir + "/" + Constants.AUDIO_DIRECTORY_NAME;
            if(directoryExist(audioDir)){
                System.out.println("FileUtil: getAudioRootDir() " + audioDir);
                return audioDir;
            }

            File file = new File(audioDir);
            boolean created = file.mkdir();
            if(created){
                System.out.println("FileUtil: getAudioRootDir() " + audioDir);
                return audioDir;
            }
        }
        return null;
    }

    public static String guessFileName(String url){
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(url);
        String fileName = URLUtil.guessFileName(url, null, fileExtension);
        System.out.println("FileUtil: guessFileName() " + fileName);
        return fileName;
    }

    public static String getExternalStoragePath(){
        String separator = "/";
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath() + separator; //instead, use getPath()
        System.out.println("FileUtil: getExternalStoragePath() " + externalStorage);
        return externalStorage;
    }

    public static boolean directoryExist(String dir){
        File file = new File(dir);
        if(file.exists() && file.isDirectory()){
            System.out.println("FileUtil: directoryExist() directory exists.");
            return true;
        }
        System.out.println("FileUtil: directoryExist() directory does not exist.");
        return false;
    }

    public static boolean deleteFile(String path){
        System.out.println("deleteFile() " + path);
        if(path != null && !path.equals("")) {
            File file = new File(path);
            file.delete();
            return true;
        }
        return false;
    }
}
