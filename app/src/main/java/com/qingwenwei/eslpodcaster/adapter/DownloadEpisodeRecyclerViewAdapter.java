package com.qingwenwei.eslpodcaster.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import java.util.ArrayList;
import java.util.List;

public class DownloadEpisodeRecyclerViewAdapter extends RecyclerView.Adapter{
    private final static String TAG = "DownloadEpisodeRecyclerViewAdapter";

    private List<PodcastEpisode> episodes;

    //adaptor constructor
    public DownloadEpisodeRecyclerViewAdapter(List<PodcastEpisode> items) {
        episodes = generateFakeData();
    }

    public static class DownloadViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public final TextView titleTextView;
        public final TextView subtitleTextView;

        public DownloadViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.downloadTitleTextView);
            subtitleTextView = (TextView) view.findViewById(R.id.downloadSubtitleTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_downloads_list, parent, false);
        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DownloadViewHolder)holder).mBoundString = episodes.get(position).getTitle();
        ((DownloadViewHolder)holder).titleTextView.setText("" + episodes.get(position).getTitle());
        ((DownloadViewHolder)holder).subtitleTextView.setText("" + episodes.get(position).getSubtitle());
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    private List<PodcastEpisode> generateFakeData(){
        List<PodcastEpisode> episodes =  new ArrayList<>();
        for(int i = 1; i < 11; i ++){
            PodcastEpisode ep = new PodcastEpisode("download title: " + i, "download subtitle: " + i);
            episodes.add(ep);
        }
        return episodes;
    }
}
