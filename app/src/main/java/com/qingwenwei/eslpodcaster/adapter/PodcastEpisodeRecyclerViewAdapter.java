package com.qingwenwei.eslpodcaster.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import java.util.List;

public class PodcastEpisodeRecyclerViewAdapter extends RecyclerView.Adapter<PodcastEpisodeRecyclerViewAdapter.ViewHolder>{
    private final static String TAG = "PodcastEpisodeRecyclerViewAdapter";

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<PodcastEpisode> mValues;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

//        public final View mView;
        public final CardView cardView;
        public final TextView titleTextView;
        public final TextView subtitleTextView;
        public final TextView pubDateTextView;
        public final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
//            mView = view;
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

    public PodcastEpisodeRecyclerViewAdapter(Context context, List<PodcastEpisode> items) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mValues = items;
    }

    @Override
    public PodcastEpisodeRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.podcast_episode_item_row, parent, false);
        view.setBackgroundResource(mBackground);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PodcastEpisodeRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.mBoundString = mValues.get(position).getTitle();
        holder.titleTextView.setText(mValues.get(position).getTitle());
        holder.subtitleTextView.setText(mValues.get(position).getSubtitle());
        holder.pubDateTextView.setText(mValues.get(position).getPubDate());
        holder.imageView.setImageResource(R.drawable.coffee);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Clicked on:" + mValues.get(position).getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
}
