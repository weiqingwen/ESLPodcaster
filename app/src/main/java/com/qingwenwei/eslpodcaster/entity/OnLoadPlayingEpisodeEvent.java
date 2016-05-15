package com.qingwenwei.eslpodcaster.entity;

public class OnLoadPlayingEpisodeEvent {
    public final PodcastEpisode playingEpisode;

    public OnLoadPlayingEpisodeEvent(PodcastEpisode episode){
        this.playingEpisode = episode;
    }
}
