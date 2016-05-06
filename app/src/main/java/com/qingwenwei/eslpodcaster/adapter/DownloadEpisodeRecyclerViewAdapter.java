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

public class DownloadEpisodeRecyclerViewAdapter extends RecyclerView.Adapter{
    private final static String TAG = "DownloadEpisodeRecyclerViewAdapter";

    private List<PodcastEpisode> episodes;
    private Context context;
    private PodcastEpisodeStatusChangeHandler handler;

    //adaptor constructor
    public DownloadEpisodeRecyclerViewAdapter() {
        episodes = new ArrayList<>();
    }

    public void setHandler(PodcastEpisodeStatusChangeHandler handler) {
        this.handler = handler;
    }

    public static class DownloadViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public final CardView cardView;
        public final TextView titleTextView;
        public final TextView subtitleTextView;

        public DownloadViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.downloadCardView);
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
        this.context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout_downloads_list, parent, false);
        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final PodcastEpisode episode = episodes.get(position);
        ((DownloadViewHolder)holder).mBoundString = episode.getTitle();
        ((DownloadViewHolder)holder).titleTextView.setText(episode.getTitle());
        ((DownloadViewHolder)holder).subtitleTextView.setText(episode.getSubtitle());

        ((DownloadViewHolder)holder).cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(episode);
                return true;
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
                "Favour this episode",
                "Delete this episode"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,episode.getTitle() + " which:" + which);
                switch (which){
                    case 0:{
                        handler.setEpisodeFavoured(episode,true);
                        break;
                    }

                    case 1:{
                        handler.setEpisodeDownloaded(episode,false);
                        break;
                    }
                }
            }
        });
        builder.show();
    }


}
