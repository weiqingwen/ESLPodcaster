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

public class FavoriteEpisodeRecyclerViewAdapter extends RecyclerView.Adapter {
    private final static String TAG = "FavoriteEpisodeRecyclerViewAdapter";

    private List<PodcastEpisode> episodes;

    //adaptor constructor
    public FavoriteEpisodeRecyclerViewAdapter(List<PodcastEpisode> items) {
        episodes = new ArrayList<>();
    }

    public static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public final CardView cardView;
        public final TextView titleTextView;
        public final TextView subtitleTextView;

        public FavoritesViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.favoriteCardView);
//            cardView.setPreventCornerOverlap(false);
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
        ((FavoritesViewHolder)holder).mBoundString = episodes.get(position).getTitle();
        ((FavoritesViewHolder)holder).titleTextView.setText("" + episodes.get(position).getTitle());
        ((FavoritesViewHolder)holder).subtitleTextView.setText("" + episodes.get(position).getSubtitle());

        ((FavoritesViewHolder)holder).cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i(TAG,"long clicked");
                return true;
            }
        });

        ((FavoritesViewHolder)holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"clicked");
            }
        });
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
