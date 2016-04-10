package com.qingwenwei.eslpodcaster.util;

/**
 * Created by qingwenwei on 2016-04-10.
 */
public interface RendererBuilder {
    void buildRenderers(AudioPlayer player);
    void cancel();
}
