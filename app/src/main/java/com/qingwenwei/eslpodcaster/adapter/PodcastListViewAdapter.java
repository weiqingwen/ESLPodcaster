package com.qingwenwei.eslpodcaster.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.entity.PodcastItem;

import java.util.ArrayList;

/**
 * Created by qingwenwei on 2016-04-09.
 */
public class PodcastListViewAdapter extends ArrayAdapter<PodcastItem> {
    private final Context context;
    private final ArrayList<PodcastItem> items;

    final String TAG = "@[PodcastListViewAdapter]";


    public PodcastListViewAdapter(Context context, int textViewResourceId, ArrayList<PodcastItem> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
    }

    private class ViewHolder{
        TextView title;
        TextView subtitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.i(TAG, " Position: " + position + "    convertView:" + convertView);

        ViewHolder holder = null;
        PodcastItem item = items.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            convertView = inflater.inflate(R.layout.podcast_row_layout, null);
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.titleTextView);
            holder.subtitle = (TextView)convertView.findViewById(R.id.subtitleTextView);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());

        return convertView;
    }
}
