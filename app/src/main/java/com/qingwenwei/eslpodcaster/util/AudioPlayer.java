package com.qingwenwei.eslpodcaster.util;

import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.TrackRenderer;
import com.qingwenwei.eslpodcaster.activity.MainActivity;

public class AudioPlayer implements ExoPlayer.Listener{
    public static final String TAG = "AudioPlayer";

    private static final int RENDERER_BUILDING_STATE_IDLE = 1;
    private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
    private static final int RENDERER_BUILDING_STATE_BUILT = 3;

    //player states
    private static final int PLAYER_STATE_IDLE = 1;
    private static final int PLAYER_STATE_PREPARING = 2;
    private static final int PLAYER_STATE_BUFFERING = 3;
    private static final int PLAYER_STATE_READY = 4;
    private static final int PLAYER_STATE_ENDED = 5;

    private ExoPlayer player;
    private RendererBuilder rendererBuilder;
    private int rendererBuildingState;
    private Context context;
    private boolean isPlaying = false;

    public AudioPlayer(Context context, RendererBuilder rendererBuilder){
        this.context = context;
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
        rendererBuilder.buildRenderer(this);
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

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    public void onPlayerStateChanged(boolean b, int state) {
        Log.i(TAG, "onPlayerStateChanged: " + state);

//        switch(state){
//            case PLAYER_STATE_READY:
//                if (isPlaying) {
//                    ((MainActivity) context).setSlidingUpPanelPlayButtonPause();
//                    Log.i(TAG, "onPlayerStateChanged() isPlaying " + isPlaying);
//                }else{
//                    ((MainActivity) context).setSlidingUpPanelPlayButtonPlaying();
//                    Log.i(TAG, "onPlayerStateChanged() not isPlaying " + isPlaying);
//                }
//                break;
//        }
    }

    @Override
    public void onPlayWhenReadyCommitted() {
        if(isPlaying){
            setPlaying(false);
            ((MainActivity) context).setSlidingUpPanelPlayButtonPlaying();
        }else{
            setPlaying(true);
            ((MainActivity) context).setSlidingUpPanelPlayButtonPause();
        }

        Log.i(TAG, "onPlayWhenReadyCommitted() isPlaying:" + isPlaying());
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {

    }
}
