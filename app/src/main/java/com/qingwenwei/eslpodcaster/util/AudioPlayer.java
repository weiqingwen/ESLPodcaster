package com.qingwenwei.eslpodcaster.util;

import android.util.Log;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.TrackRenderer;

/**
 * Created by qingwenwei on 2016-04-10.
 */
public class AudioPlayer implements ExoPlayer.Listener{

    final String TAG = "AudioPlayer";

    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;

    private ExoPlayer player;
    private RendererBuilder rendererBuilder;
    private int rendererBuildingState;

    public AudioPlayer(RendererBuilder rendererBuilder){
        this.rendererBuilder = rendererBuilder;
        this.player = ExoPlayer.Factory.newInstance(1);
        this.player.addListener(this);
        this.rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
    }

    void onRenderer(TrackRenderer renderer){
        if (renderer != null){
            player.prepare(renderer);
            this.rendererBuildingState = RENDERER_BUILDING_STATE_BUILT;
        }
    }

    public void prepare(){
        if(rendererBuildingState == RENDERER_BUILDING_STATE_BUILT){
            player.stop();
        }
        rendererBuilder.cancel();
        rendererBuildingState = RENDERER_BUILDING_STATE_BUILDING;
        rendererBuilder.buildRenderers(this);
    }

    public void setPlayWhenReady(boolean playWhenReady){
        player.setPlayWhenReady(playWhenReady);
    }

    public void seekTo(long positionMs){
        player.seekTo(positionMs);
    }

    public void release(){
        rendererBuilder.cancel();
        rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
        player.release();
    }

    public long getDuration(){
        return player.getDuration();
    }

    public long getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public void foo(){
//        long durationMillis = player.getDuration();
//        long currPosMillis = player.getCurrentPosition();
//        String duration = toMinutesString(durationMillis);
//        String currPos = toMinutesString(currPosMillis);
//
//        Log.i(TAG, " CurrPos: " + currPos + "  Duration:" + duration);
//        Log.i(TAG,"Duration: " + getDuration());

//        Log.i(TAG,"" + player.getPlaybackState());
//        player.addListener();
    }


    @Override
    public void onPlayerStateChanged(boolean b, int i) {
        Log.i(TAG, "onPlayerStateChanged: " + i);

        //STATE_ENDED
        if(i == 5){

        }
    }

    @Override
    public void onPlayWhenReadyCommitted() {

    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {

    }
}
