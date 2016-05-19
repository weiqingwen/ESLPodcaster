package com.qingwenwei.eslpodcaster.event;

import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

public class OnLoadPlayingEpisodeEvent {
    public final PodcastEpisode playingEpisode;

    public OnLoadPlayingEpisodeEvent(PodcastEpisode episode){
        this.playingEpisode = episode;
    }
}
