package com.qingwenwei.eslpodcaster.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.activity.MainActivity;
import com.qingwenwei.eslpodcaster.adapter.PodcastListAdapter;
import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.event.OnLoadPlayingEpisodeEvent;
import com.qingwenwei.eslpodcaster.util.PodcastEpisodeListParser;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PodcastListFragment extends Fragment
        implements View.OnClickListener{

    private final static String TAG = "PodcastListFragment";
    private boolean dataInitialized = false;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private PodcastListAdapter adapter;
    private List<PodcastEpisode> episodes;
    private int currNumEpisodes = 0;

    //load more items
    private boolean loadingMoreItems;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 1;// The minimum amount of items to have below your current scroll position before loading more.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the panel_layout_sliding_up_player for this fragment
        Log.i(TAG, "onCreateView()");

        mSwipeRefreshLayout = (SwipeRefreshLayout)inflater.inflate(R.layout.fragment_podcasts, container, false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        recyclerView = (RecyclerView) mSwipeRefreshLayout.findViewById(R.id.podcastRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new DownloadEpisodesAsyncTask(true).execute(Constants.ESLPOD_ALL_EPISODE_URL);
            }
        });

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
                            Log.i(TAG,"End has been reached, start to load more...");
                            new LoadMoreEpisodesListener().onLoadMore();
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
            adapter = new PodcastListAdapter(episodes);
            adapter.setOnCardViewClickListener(this);
            recyclerView.setAdapter(adapter);
            new DownloadEpisodesAsyncTask(false).execute(Constants.ESLPOD_ALL_EPISODE_URL);
        }

        return mSwipeRefreshLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (((MainActivity)getActivity()).getSlidingUpPanelLayout().getChildAt(1).hasOnClickListeners()) {
            ((MainActivity)getActivity()).getSlidingUpPanelLayout().getChildAt(1).setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //load playing episode
            case R.id.podcastCardView:
                PodcastEpisode episode = (PodcastEpisode) v.getTag();
                EventBus.getDefault().post(new OnLoadPlayingEpisodeEvent(episode));
                break;
        }
    }

    // helpers
    private class DownloadEpisodesAsyncTask extends AsyncTask<String, Integer, ArrayList<PodcastEpisode>> {
        private static final String TAG = "DownloadEpisodesAsyncTask";
        private boolean isRefreshing = false;

        //constructor
        public DownloadEpisodesAsyncTask(boolean isRefreshing){
            this.isRefreshing = isRefreshing;
        }

        @Override
        protected ArrayList<PodcastEpisode> doInBackground(String... urls) {
            ArrayList<PodcastEpisode> episodes =
                    (ArrayList<PodcastEpisode>) new PodcastEpisodeListParser().parseEpisodes(urls[0] + 0);
            return episodes;
        }

        @Override
        protected void onPostExecute(final ArrayList<PodcastEpisode> downloadedEpisodes) {
            Log.i(TAG, "onPostExecute()  downloaded items: " + downloadedEpisodes.size());
            episodes = downloadedEpisodes;
            currNumEpisodes = episodes.size();
            adapter.updateEpisodes(episodes);
            dataInitialized = true;
            if (isRefreshing){
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    //load more items async task
    private class DownloadMoreEpisodesAsyncTask extends AsyncTask<String, Integer, ArrayList<PodcastEpisode>> {
        private static final String TAG = "DownloadMoreEpisodesAsyncTask";

        @Override
        protected ArrayList<PodcastEpisode> doInBackground(String... urls) {
            ArrayList<PodcastEpisode> episodes = (ArrayList<PodcastEpisode>)
                    new PodcastEpisodeListParser().parseEpisodes(urls[0] + currNumEpisodes);
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

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    private class LoadMoreEpisodesListener implements OnLoadMoreListener {
        private static final String TAG = "LoadMoreEpisodesListener";
        @Override
        public void onLoadMore() {
            Log.i(TAG, "onLoadMore()");
            episodes.add(null);
            adapter.updateEpisodes(episodes);
            new DownloadMoreEpisodesAsyncTask().execute(Constants.ESLPOD_ALL_EPISODE_URL);
        }
    }

    public void setLoaded() {
        this.loadingMoreItems = false;
    }

}
