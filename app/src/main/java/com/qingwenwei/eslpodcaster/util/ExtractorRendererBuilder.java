package com.qingwenwei.eslpodcaster.util;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

/**
 * Created by qingwenwei on 2016-04-10.
 */
public class ExtractorRendererBuilder implements RendererBuilder{

    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    private final Context context;
    private final String userAgent;
    private final String url;

    public ExtractorRendererBuilder(Context context, String userAgent, String url){
        this.context = context;
        this.userAgent = userAgent;
        this.url = url;
    }

    @Override
    public void buildRenderers(AudioPlayer player) {
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                Uri.parse(url),
                new DefaultUriDataSource(context, userAgent),
                new DefaultAllocator(BUFFER_SEGMENT_SIZE),
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);

        TrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(
                sampleSource,
                MediaCodecSelector.DEFAULT);

        player.onRenderer(audioRenderer);
    }

    @Override
    public void cancel() {
        //do nothing
    }
}
