package com.qingwenwei.eslpodcaster.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.util.AudioPlayer;
import com.qingwenwei.eslpodcaster.util.ExtractorRendererBuilder;
import com.qingwenwei.eslpodcaster.util.RendererBuilder;

import java.util.concurrent.TimeUnit;

public class DeprecatedAudioPlayerActivity extends AppCompatActivity {
    private final String TAG = "DeprecatedAudioPlayerActivity";
    private final String USER_AGENT = "ESLPodcaster";

    private Button playButton;
    private Button hideButton;
    private TextView currPosTextView;
    private TextView durationTextView;
    private SeekBar seekBar;

    private AudioPlayer player;
    private PodcastEpisode podcastItem;
    private boolean playIsPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        playButton = (Button)findViewById(R.id.playButton);
        currPosTextView = (TextView)findViewById(R.id.currPosTextView);
        durationTextView = (TextView)findViewById(R.id.durationTextView);
        hideButton = (Button)findViewById(R.id.hideButton);

        podcastItem = (PodcastEpisode)getIntent().getSerializableExtra(Constants.PODCAST_ITEM_INTENT_TAG);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if(playIsPlaying) {
                        currPosTextView.setText(toMinuteFormat(progress));
                        player.seekTo(progress);
                    }else{
                        currPosTextView.setText(toMinuteFormat(progress));
                        player.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //do nothing
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playIsPlaying) {
                    player.setPlayWhenReady(true);
                    seekBar.setMax((int) player.getDuration());
                    durationTextView.setText(toMinuteFormat(player.getDuration()));
                    playIsPlaying = true;
                    playButton.setText("Pause");
                    seekBar.postDelayed(onEverySecond, 1000);
                } else {
                    player.setPlayWhenReady(false);
                    playIsPlaying = false;
                    playButton.setText("Play");
                }
            }
        });


        hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        preparePlayer();
    }

    // internal functions
    private Runnable onEverySecond = new Runnable() {
        @Override
        public void run() {
        if(playIsPlaying){
            currPosTextView.setText(toMinuteFormat(player.getCurrentPosition()));
            seekBar.setProgress((int)player.getCurrentPosition());
            seekBar.postDelayed(onEverySecond, 1000);
        }
        }
    };

    private void preparePlayer(){
        if(player == null){
            RendererBuilder rendererBuilder = new ExtractorRendererBuilder(getBaseContext(),USER_AGENT,podcastItem.audioFileUrl);
            player = new AudioPlayer(this,rendererBuilder);
            player.prepare();
        }
    }

    private void releasePlayer(){
        if(player != null){
            player.release();
            player = null;
        }
    }

    //helper
    private String toMinuteFormat(long millis){
        return String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
