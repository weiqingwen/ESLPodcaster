package com.qingwenwei.eslpodcaster.listener;


import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.qingwenwei.eslpodcaster.activity.AudioPlayerActivity;
import com.qingwenwei.eslpodcaster.entity.PodcastItem;


/**
 * Created by qingwenwei on 2016-04-10.
 */
public class PodcastItemOnClickListener implements AdapterView.OnItemClickListener {

    final String TAG = "@[PodcastItemOnClickListener]";
    private static final String PODCAST_ITEM_DATA = "PODCAST_ITEM_DATA";

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        PodcastItem podcastItem = (PodcastItem)parent.getItemAtPosition(position);
        Log.i(TAG, " title=>" + podcastItem.getTitle());

        Intent intent = new Intent(view.getContext(),AudioPlayerActivity.class);
        intent.putExtra(PODCAST_ITEM_DATA,podcastItem);
        view.getContext().startActivity(intent);
    }


}
