package com.qingwenwei.eslpodcaster.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qingwenwei.eslpodcaster.R;

public class AudioPlayerActivity extends AppCompatActivity {
    final String TAG = "AudioPlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
    }
}
