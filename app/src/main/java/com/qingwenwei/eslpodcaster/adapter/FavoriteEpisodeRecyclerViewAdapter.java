package com.qingwenwei.eslpodcaster.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    public FavoriteEpisodeRecyclerViewAdapter(List<PodcastEpisode> items) {
        episodes = generateFakeData();
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

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_favorites_list, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((FavoritesViewHolder)holder).titleTextView.setText("" + episodes.get(position));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
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
