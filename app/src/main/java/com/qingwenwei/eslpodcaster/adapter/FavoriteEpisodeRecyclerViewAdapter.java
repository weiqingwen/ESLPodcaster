package com.qingwenwei.eslpodcaster.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.listener.PodcastEpisodeStatusChangeHandler;

import java.util.ArrayList;
import java.util.List;

public class FavoriteEpisodeRecyclerViewAdapter extends RecyclerView.Adapter {
    private final static String TAG = "FavoriteEpisodeRecyclerViewAdapter";

    private List<PodcastEpisode> episodes;
    private Context context;
    private PodcastEpisodeStatusChangeHandler handler;

    //adaptor constructor
    public FavoriteEpisodeRecyclerViewAdapter() {
        this.episodes = new ArrayList<>();
    }

    public void setHandler(PodcastEpisodeStatusChangeHandler handler) {
        this.handler = handler;
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
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout_favorites_list, parent, false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final PodcastEpisode episode = episodes.get(position);
        ((FavoritesViewHolder)holder).mBoundString = episode.getTitle();
        ((FavoritesViewHolder)holder).titleTextView.setText(episode.getTitle());
        ((FavoritesViewHolder)holder).subtitleTextView.setText(episode.getSubtitle());

        ((FavoritesViewHolder)holder).cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(episode);
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

    private void showPopupMenu(final PodcastEpisode episode){
        CharSequence items[] = new CharSequence[] {
                "Download this episode",
                "Disfavour this episode"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,episode.getTitle() + " which:" + which);
                switch (which){
                    case 0:{
                        handler.setEpisodeDownloaded(episode,true);
                        break;
                    }

                    case 1:{
                        handler.setEpisodeFavoured(episode,false);
                        break;
                    }
                }
            }
        });
        builder.show();
    }
}
