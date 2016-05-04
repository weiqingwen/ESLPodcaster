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
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.util.SQLiteHelper;

import java.util.ArrayList;

public class DownloadedFragment extends Fragment {
    private static final String TAG = "DownloadedFragment";

    private RecyclerView recyclerView;
    private DownloadEpisodeRecyclerViewAdapter adapter;

    public DownloadedFragment() {
        // Required empty public constructor
        adapter = new DownloadEpisodeRecyclerViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_downloads, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        update();
        return recyclerView;
    }

    public void update(){
        new GetAllDownloadedEpisodesAsyncTask().execute();
    }

    private class GetAllDownloadedEpisodesAsyncTask extends AsyncTask<Void, Void, ArrayList<PodcastEpisode>> {
        private static final String TAG = "GetAllDownloadedEpisodesAsyncTask";
        @Override
        protected ArrayList<PodcastEpisode> doInBackground(Void... params) {
            SQLiteHelper db = new SQLiteHelper(getContext());
            ArrayList<PodcastEpisode> episodes = (ArrayList<PodcastEpisode>) db.getAllDownloadEpisodes();
            return episodes;
        }

        @Override
        protected void onPostExecute(ArrayList<PodcastEpisode> podcastEpisodes) {
            adapter.updateEpisodes(podcastEpisodes);
            recyclerView.setAdapter(adapter);
            Log.i(TAG," data size:" + podcastEpisodes.size());
        }
    }

}
