package com.qingwenwei.eslpodcaster.util;

public interface RendererBuilder {
    void buildRenderer(AudioPlayer player);
    void cancel();
}
