package com.qingwenwei.eslpodcaster.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.adapter.PodcastEpisodeRecyclerViewAdapter;
import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.util.EslPodWebParser;

import java.util.ArrayList;
import java.util.List;

public class PodcastListFragment extends Fragment {
    private final static String TAG = "PodcastListFragment";
    private boolean dataInitialized = false;

    private RecyclerView recyclerView;

    private PodcastEpisodeRecyclerViewAdapter adapter;
    private List<PodcastEpisode> episodes;
    private int currNumEpisodes = 0;

    //load more items
//    private OnLoadMoreListener onLoadMoreListener;
    private boolean loadingMoreItems;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 1;// The minimum amount of items to have below your current scroll position before loading more.

    public PodcastListFragment(){
        Log.i(TAG,"PodcastListFragment()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView()");

        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_podcast_list, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
//        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    Log.i(TAG,"onScrolled() totalItemCount:" + totalItemCount + "   lastVisibleItem:" + lastVisibleItem);
                    if (!loadingMoreItems){
                        if( totalItemCount <= lastVisibleItem + visibleThreshold) {
                             Log.i(TAG,"End has been reached");
                             if (adapter.getOnLoadMoreListener() != null) {
                                 adapter.getOnLoadMoreListener().onLoadMore();
                             }
                             loadingMoreItems = true;
                         }
                    }
                }
            }
        );

        if(dataInitialized) {
            //just refresh the recyclerView as the episode date was initialized
            recyclerView.setAdapter(adapter);
        }else {
            //first time download the episode data
            episodes = new ArrayList<>();
            adapter = new PodcastEpisodeRecyclerViewAdapter(episodes,recyclerView);
            adapter.setOnLoadMoreListener(new LoadMoreEpisodesListener());
            recyclerView.setAdapter(adapter);
            new DownloadEpisodesAsyncTask().execute(Constants.ESLPOD_ALL_EPISODE_URL);
        }

        return recyclerView;
    }

    // helpers
    private class DownloadEpisodesAsyncTask extends AsyncTask<String, Integer, ArrayList<PodcastEpisode>> {
        private static final String TAG = "DownloadEpisodesAsyncTask";

        @Override
        protected ArrayList<PodcastEpisode> doInBackground(String... urls) {
            ArrayList<PodcastEpisode> episodes =
                    (ArrayList<PodcastEpisode>) new EslPodWebParser().parserEpisodes(urls[0] + 0);
            return episodes;
        }

        @Override
        protected void onPostExecute(final ArrayList<PodcastEpisode> downloadedEpisodes) {
            Log.i(TAG, "onPostExecute()  downloaded items: " + downloadedEpisodes.size());
            episodes = downloadedEpisodes;
            currNumEpisodes = episodes.size();
            adapter.updateEpisodes(episodes);
            dataInitialized = true;
        }

    }

    private class DownloadMoreEpisodesAsyncTask extends AsyncTask<String, Integer, ArrayList<PodcastEpisode>> {
        private static final String TAG = "DownloadMoreEpisodesAsyncTask";

        @Override
        protected ArrayList<PodcastEpisode> doInBackground(String... urls) {
            ArrayList<PodcastEpisode> episodes =
                    (ArrayList<PodcastEpisode>) new EslPodWebParser().parserEpisodes(urls[0] + currNumEpisodes);
            return episodes;
        }

        @Override
        protected void onPostExecute(final ArrayList<PodcastEpisode> downloadedEpisodes) {
            Log.i(TAG, "onPostExecute()  downloaded more items: " + downloadedEpisodes.size());
            episodes.remove(episodes.size() - 1);
            episodes.addAll(downloadedEpisodes);
            currNumEpisodes = episodes.size();
            adapter.updateEpisodes(episodes);
            setLoaded();
        }

    }

    private class LoadMoreEpisodesListener implements PodcastEpisodeRecyclerViewAdapter.OnLoadMoreListener {
        @Override
        public void onLoadMore() {
            Log.i(TAG, "onLoadMore()");
            episodes.add(null);
            adapter.updateEpisodes(episodes);
//            adapter.notifyItemInserted(episodes.size());
            new DownloadMoreEpisodesAsyncTask().execute(Constants.ESLPOD_ALL_EPISODE_URL);
        }
    }

    public void setLoaded() {
        this.loadingMoreItems = false;
    }
}
