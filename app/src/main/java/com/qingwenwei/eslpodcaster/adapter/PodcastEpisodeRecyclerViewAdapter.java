package com.qingwenwei.eslpodcaster.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import java.util.List;

//public class PodcastEpisodeRecyclerViewAdapter extends RecyclerView.Adapter<PodcastEpisodeRecyclerViewAdapter.EpisodeViewHolder>{
public class PodcastEpisodeRecyclerViewAdapter extends RecyclerView.Adapter{
    private final static String TAG = "PodcastEpisodeRecyclerViewAdapter";

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<PodcastEpisode> episodes;

    ///
    //load more items
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean loadingMoreItems;
    private int lastVisibleItem, totalItemCount;

    // The minimum amount of items to have below your current scroll position before loading more.
    private int visibleThreshold = 1;


    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public final CardView cardView;
        public final TextView titleTextView;
        public final TextView subtitleTextView;
        public final TextView pubDateTextView;
        public final ImageView imageView;

        public EpisodeViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardView);
            cardView.setPreventCornerOverlap(false);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            subtitleTextView = (TextView) view.findViewById(R.id.subtitleTextView);
            pubDateTextView = (TextView) view.findViewById(R.id.pubDateTextView);
            imageView = (ImageView) view.findViewById(R.id.cardViewCategoryImageView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText();
        }
    }

    ///
    // load more items
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.loadMoreProgressBar);
        }
    }

    //constructor
    public PodcastEpisodeRecyclerViewAdapter(Context context, List<PodcastEpisode> items, RecyclerView recyclerView) {
//        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        episodes = items;

        //setup onScroll listener to load more items
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    Log.i(TAG,"onScrolled() totalItemCount:" + totalItemCount + "   lastVisibleItem:" + lastVisibleItem);
                    if (!loadingMoreItems
                        && totalItemCount <= lastVisibleItem + visibleThreshold) {
                        Log.i(TAG,"End has been reached");
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loadingMoreItems = true;
                    }
                }
            }
        );
    }

    @Override
    public int getItemViewType(int position) {
        return episodes.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.podcast_episode_item_row, parent, false);
//        view.setBackgroundResource(mBackground);
//        return new EpisodeViewHolder(view);

        RecyclerView.ViewHolder vh;
        if(viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.podcast_episode_item_row, parent, false);
            view.setBackgroundResource(mBackground);
            vh = new EpisodeViewHolder(view);
        }else{
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_loading_item, parent, false);
            vh = new ProgressViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof EpisodeViewHolder) {
            ((EpisodeViewHolder)holder).mBoundString = episodes.get(position).getTitle();
            ((EpisodeViewHolder)holder).titleTextView.setText(episodes.get(position).getTitle());
            ((EpisodeViewHolder)holder).subtitleTextView.setText(episodes.get(position).getSubtitle());
            ((EpisodeViewHolder)holder).pubDateTextView.setText(episodes.get(position).getPubDate());
            ((EpisodeViewHolder)holder).imageView.setImageResource(R.drawable.coffee);
            ((EpisodeViewHolder)holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Clicked on:" + episodes.get(position).getTitle());
                }
            });
        }else {
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }


    ///
    // load more items
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        this.loadingMoreItems = false;
    }

    public void updateEpisodes(List<PodcastEpisode> newEpisodes){
        this.episodes.clear();
        this.episodes.addAll(newEpisodes);
        this.notifyDataSetChanged();
    }
}