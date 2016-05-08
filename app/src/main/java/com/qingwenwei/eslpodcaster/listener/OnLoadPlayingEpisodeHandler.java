package com.qingwenwei.eslpodcaster.listener;

import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

public interface OnLoadPlayingEpisodeHandler {
    void loadPlayingEpisode(PodcastEpisode episode);
}
