package com.qingwenwei.eslpodcaster.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import java.util.ArrayList;
import java.util.List;

public class FavoriteEpisodeRecyclerViewAdapter extends RecyclerView.Adapter {
    private final static String TAG = "FavoriteEpisodeRecyclerViewAdapter";

    private List<PodcastEpisode> episodes;

    //adaptor constructor
    public FavoriteEpisodeRecyclerViewAdapter(List<PodcastEpisode> items, RecyclerView recyclerView) {
        episodes = generateFakeData();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public final TextView titleTextView;
        public final TextView subtitleTextView;

        public FavoritesViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.favoriteTitleTextView);
            subtitleTextView = (TextView) view.findViewById(R.id.favoriteSubtitleTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText();
        }
    }

    private List<PodcastEpisode> generateFakeData(){
        List<PodcastEpisode> episodes =  new ArrayList<>();
        for(int i = 1; i < 11; i ++){
            PodcastEpisode ep = new PodcastEpisode("title:" + i, "subtitle:" + i);
            episodes.add(ep);
        }
        return episodes;
    }
}
