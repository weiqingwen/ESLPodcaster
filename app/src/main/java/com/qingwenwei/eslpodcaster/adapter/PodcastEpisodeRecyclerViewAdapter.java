package com.qingwenwei.eslpodcaster.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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

public class PodcastEpisodeRecyclerViewAdapter extends RecyclerView.Adapter{
    private final static String TAG = "PodcastEpisodeRecyclerViewAdapter";

    private final int ICON_RELATIONSHIP = R.drawable.category_icon_relationship;
    private final int ICON_DINING = R.drawable.category_icon_dining;
    private final int ICON_ENGLISH_CAFE = R.drawable.category_icon_coffee;
    private final int ICON_DAILY_LIFE = R.drawable.category_icon_daily;
    private final int ICON_SHOPPING = R.drawable.category_icon_shopping;
    private final int ICON_HEALTH = R.drawable.category_icon_health;
    private final int ICON_TRAVEL = R.drawable.category_icon_travel;
    private final int ICON_TRANSPORTATION = R.drawable.category_icon_transportation;
    private final int ICON_BUSINESS = R.drawable.category_icon_business;
    private final int ICON_ENTERTAINMENT = R.drawable.category_icon_entertainment;

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<PodcastEpisode> episodes;

    //item type (normal CardView and load more view)
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private View.OnClickListener onCardViewClickListener;

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;
        public final CardView cardView;
        public final TextView titleTextView;
        public final TextView subtitleTextView;
        public final TextView pubDateTextView;
        public final TextView categoryTextView;
        public final ImageView iconImageView;

        public EpisodeViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.podcastCardView);
//            cardView.setPreventCornerOverlap(false);
            titleTextView = (TextView) view.findViewById(R.id.cardViewTitleTextView);
            subtitleTextView = (TextView) view.findViewById(R.id.cardViewSubtitleTextView);
            pubDateTextView = (TextView) view.findViewById(R.id.cardViewPubDateTextView);
            categoryTextView = (TextView) view.findViewById(R.id.cardViewCategoryTextView);
            iconImageView = (ImageView) view.findViewById(R.id.cardViewIconImageView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText();
        }
    }
    
    // load more items
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.loadMoreProgressBar);
        }
    }

    //adaptor constructor
    public PodcastEpisodeRecyclerViewAdapter(List<PodcastEpisode> items) {
        this.mBackground = mTypedValue.resourceId;
        this.episodes = items;
    }

    @Override
    public int getItemViewType(int position) {
        return episodes.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_podcasts_list, parent, false);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof EpisodeViewHolder) {
            PodcastEpisode episode = episodes.get(position);
            ((EpisodeViewHolder)holder).mBoundString = episode.getTitle();
            ((EpisodeViewHolder)holder).titleTextView.setText(episode.getTitle());
            ((EpisodeViewHolder)holder).subtitleTextView.setText(episode.getSubtitle());
            ((EpisodeViewHolder)holder).pubDateTextView.setText(episode.getPubDate());
            ((EpisodeViewHolder)holder).categoryTextView.setText("Tags: " + episode.getCategory());

            String category = episode.getCategory();
            int iconToSet = 0;
            if(match("relationships",category)){
                iconToSet = ICON_RELATIONSHIP;
            }else if(match("dining",category)){
                iconToSet = ICON_DINING;
            }else if(match("english caf",category)){
                iconToSet = ICON_ENGLISH_CAFE;
            }else if(match("daily life",category)){
                iconToSet = ICON_DAILY_LIFE;
            }else if(match("shopping",category)){
                iconToSet = ICON_SHOPPING;
            }else if(match("health/medicine",category)){
                iconToSet = ICON_HEALTH;
            }else if(match("travel",category)){
                iconToSet = ICON_TRAVEL;
            }else if(match("transportation",category)){
                iconToSet = ICON_TRANSPORTATION;
            }else if(match("business",category)){
                iconToSet = ICON_BUSINESS;
            }else if(match("entertainment",category)){
                iconToSet = ICON_ENTERTAINMENT;
            }
            ((EpisodeViewHolder) holder).iconImageView.setImageResource(iconToSet);
            ((EpisodeViewHolder)holder).cardView.setOnClickListener(onCardViewClickListener);
            ((EpisodeViewHolder)holder).cardView.setTag(episode);

        }else {
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    //helper match
    private boolean match(String str1, String str2){
        if(str1.toLowerCase().contains(str2.toLowerCase())){
            return true;
        }else if(str2.toLowerCase().contains(str1.toLowerCase())){
            return true;
        }
        return false;
    }

    public void updateEpisodes(List<PodcastEpisode> newEpisodes){
        this.episodes.clear();
        this.episodes.addAll(newEpisodes);
        this.notifyDataSetChanged();
    }

    public void setOnCardViewClickListener(View.OnClickListener listener) {
        this.onCardViewClickListener = listener;
    }
}
