package com.qingwenwei.eslpodcaster.db;

public class DBConstants {
    public static abstract class EpisodeColumn{
        //table name
        public static final String TABLE_EPISODES = "EPISODES";
        //table columns
        public static final String KEY_ID = "id";
        public static final String KEY_TITLE = "title";
        public static final String KEY_SUBTITLE = "subtitle";
        public static final String KEY_CONTENT= "content";
        public static final String KEY_CATEGORY = "category";
        public static final String KEY_PUB_DATE = "pub_date";
        public static final String KEY_AUDIO_URL = "audio_url";
        public static final String KEY_WEB_URL = "web_url";
        public static final String KEY_ARCHIVED = "archived";
        public static final String KEY_LOCAL_AUDIO_FILE = "local_audio_file";
    }
}
