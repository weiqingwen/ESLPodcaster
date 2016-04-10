package com.qingwenwei.eslpodcaster.listener;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.qingwenwei.eslpodcaster.entity.PodcastItem;

import java.util.Locale;

/**
 * Created by qingwenwei on 2016-04-10.
 */
public class PodcastItemOnClickListener implements AdapterView.OnItemClickListener {

    private String TAG = "@[PodcastItemOnClickListener]";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        PodcastItem item = (PodcastItem)parent.getItemAtPosition(position);
        Log.i(TAG," title=>"+item.getTitle());

    }
}
