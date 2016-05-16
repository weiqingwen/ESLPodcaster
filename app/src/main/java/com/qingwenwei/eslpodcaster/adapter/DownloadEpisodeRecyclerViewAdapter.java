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

public class DownloadEpisodeRecyclerViewAdapter extends RecyclerView.Adapter{
    private final static String TAG = "DownloadEpisodeRecyclerViewAdapter";

    private List<PodcastEpisode> episodes;

    private View.OnLongClickListener onCardViewLongClickListener;
    private View.OnClickListener onCardViewClickListener;

    private final int VIEW_ITEM = 1;
    private final int VIEW_EMPTY = 0;

    public DownloadEpisodeRecyclerViewAdapter() {
        episodes = new ArrayList<>();
    }

    //listener setters
    public void setOnCardViewClickListener(View.OnClickListener listener) {
        this.onCardViewClickListener = listener;
    }

    public void setOnCardViewLongClickListener(View.OnLongClickListener listener) {
        this.onCardViewLongClickListener = listener;
    }

    public static class DownloadViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public final CardView cardView;
        public final TextView titleTextView;
        public final TextView subtitleTextView;
        public final TextView downloadedDateTextView;

        public DownloadViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.downloadCardView);
            titleTextView = (TextView) view.findViewById(R.id.downloadTitleTextView);
            subtitleTextView = (TextView) view.findViewById(R.id.downloadSubtitleTextView);
            downloadedDateTextView = (TextView) view.findViewById(R.id.downloadDateTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText();
        }
    }

    public static class NoDataViewHolder extends RecyclerView.ViewHolder {
        public final TextView noDataHintTextView;

        public NoDataViewHolder(View view) {
            super(view);
            noDataHintTextView = (TextView) view.findViewById(R.id.noDataHintTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + noDataHintTextView.getText();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return episodes.get(position) != null ? VIEW_ITEM : VIEW_EMPTY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG,"onCreateViewHolder()");
//        View view = LayoutInflater.from(parent.getContext()).inflate(
//                R.layout.row_layout_downloads_list, parent, false);
//        return new DownloadViewHolder(view);

        RecyclerView.ViewHolder viewHolder;
        if(viewType == VIEW_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_layout_downloads_list, parent, false);
            viewHolder = new DownloadViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_no_date_item, parent, false);
            viewHolder = new NoDataViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        if(holder instanceof DownloadViewHolder){
            final PodcastEpisode episode = episodes.get(position);
            ((DownloadViewHolder)holder).mBoundString = episode.getTitle();
            ((DownloadViewHolder)holder).titleTextView.setText(episode.getTitle());
            ((DownloadViewHolder)holder).subtitleTextView.setText(episode.getSubtitle());
            ((DownloadViewHolder)holder).downloadedDateTextView.setText(episode.getDownloadedDate());
            ((DownloadViewHolder)holder).cardView.setOnLongClickListener(onCardViewLongClickListener);
            ((DownloadViewHolder)holder).cardView.setOnClickListener(onCardViewClickListener);
            ((DownloadViewHolder)holder).cardView.setTag(episode);
        }
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
