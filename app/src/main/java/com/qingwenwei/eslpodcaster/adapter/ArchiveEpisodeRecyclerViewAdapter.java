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

    public static class ArchiveViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public final CardView cardView;
        public final TextView titleTextView;
        public final TextView subtitleTextView;
        public final TextView archivedDateTextView;

        public ArchiveViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.archiveCardView);
            titleTextView = (TextView) view.findViewById(R.id.archiveTitleTextView);
            subtitleTextView = (TextView) view.findViewById(R.id.archiveSubtitleTextView);
            archivedDateTextView = (TextView) view.findViewById(R.id.archiveDateTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText();
        }
    }

    @Override
    public ArchiveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG,"onCreateViewHolder()");
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_layout_archives_list,
                parent,
                false);
        return new ArchiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final PodcastEpisode episode = episodes.get(position);
        ((ArchiveViewHolder)holder).mBoundString = episode.getTitle();
        ((ArchiveViewHolder)holder).titleTextView.setText(episode.getTitle());
        ((ArchiveViewHolder)holder).subtitleTextView.setText(episode.getSubtitle());
        ((ArchiveViewHolder)holder).archivedDateTextView.setText(episode.getArchivedDate());
        ((ArchiveViewHolder)holder).cardView.setOnLongClickListener(onCardViewLongClickListener);
        ((ArchiveViewHolder)holder).cardView.setOnClickListener(onCardViewClickListener);
        ((ArchiveViewHolder)holder).cardView.setTag(episode);
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
