package com.qingwenwei.eslpodcaster.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import java.util.ArrayList;
import java.util.List;

public class ArchiveEpisodeRecyclerViewAdapter extends RecyclerView.Adapter {
    private final static String TAG = "ArchiveEpisodeRecyclerViewAdapter";

    private List<PodcastEpisode> episodes;

    private View.OnLongClickListener onCardViewLongClickListener;
    private View.OnClickListener onCardViewClickListener;

    //adaptor constructor
    public ArchiveEpisodeRecyclerViewAdapter() {
        this.episodes = new ArrayList<>();
    }

    //handler setters
    public void setOnCardViewLongClickListener(View.OnLongClickListener listener) {
        this.onCardViewLongClickListener = listener;
    }

    public void setOnCardViewClickListener(View.OnClickListener listener) {
        this.onCardViewClickListener = listener;
    }

    public static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public final CardView cardView;
        public final TextView titleTextView;
        public final TextView subtitleTextView;

        public FavoritesViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.favoriteCardView);
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
        Log.i(TAG,"onCreateViewHolder()");

//        this.context = parent.getContext();
//        View view = LayoutInflater.from(context).inflate(R.layout.row_layout_favorites_list, parent, false);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_favorites_list, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final PodcastEpisode episode = episodes.get(position);
        ((FavoritesViewHolder)holder).mBoundString = episode.getTitle();
        ((FavoritesViewHolder)holder).titleTextView.setText(episode.getTitle());
        ((FavoritesViewHolder)holder).subtitleTextView.setText(episode.getSubtitle());
        ((FavoritesViewHolder)holder).cardView.setOnLongClickListener(onCardViewLongClickListener);
        ((FavoritesViewHolder)holder).cardView.setOnClickListener(onCardViewClickListener);
        ((FavoritesViewHolder)holder).cardView.setTag(episode);
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void updateEpisodes(List<PodcastEpisode> newEpisodes){
        this.episodes.clear();
        this.episodes.addAll(newEpisodes);
        this.notifyDataSetChanged();
    }
}
