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
import com.qingwenwei.eslpodcaster.adapter.DownloadEpisodeRecyclerViewAdapter;
import com.qingwenwei.eslpodcaster.db.EpisodeDAO;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.listener.OnEpisodeStatusChangeHandler;
import com.qingwenwei.eslpodcaster.listener.OnLoadPlayingEpisodeHandler;

import java.util.ArrayList;

public class DownloadFragment extends Fragment {
    private static final String TAG = "DownloadFragment";

    private RecyclerView recyclerView;
    private DownloadEpisodeRecyclerViewAdapter adapter;

    public DownloadFragment() {
        // Required empty public constructor
        adapter = new DownloadEpisodeRecyclerViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_downloads, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    //handler setters
    public void setOnEpisodeStatusChangeHandler(OnEpisodeStatusChangeHandler handler){
        adapter.setOnEpisodeStatusChangeHandler(handler);
    }

    public void setOnLoadPlayingEpisodeHandler(OnLoadPlayingEpisodeHandler handler){
        adapter.setOnLoadPlayingEpisodeHandler(handler);
    }


    public void refresh(){
        new GetAllDownloadedEpisodesAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class GetAllDownloadedEpisodesAsyncTask extends AsyncTask<Void, Void, ArrayList<PodcastEpisode>> {
        private static final String TAG = "GetAllDownloadedEpisodesAsyncTask";
        @Override
        protected ArrayList<PodcastEpisode> doInBackground(Void... params) {
//            SQLiteHelper db = new SQLiteHelper(getContext());
//            ArrayList<PodcastEpisode> episodes = (ArrayList<PodcastEpisode>) db.getAllDownloadedEpisodes();

            EpisodeDAO dao = new EpisodeDAO(getContext());
            ArrayList downloads = (ArrayList) dao.getAllDownloadedEpisodes();

            return downloads;
        }

        @Override
        protected void onPostExecute(ArrayList<PodcastEpisode> podcastEpisodes) {
            adapter.updateEpisodes(podcastEpisodes);
            Log.i(TAG,"Refreshed download list size: " + podcastEpisodes.size());

        }
    }

}
