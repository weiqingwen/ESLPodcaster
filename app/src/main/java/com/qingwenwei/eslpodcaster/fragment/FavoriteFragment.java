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
import com.qingwenwei.eslpodcaster.adapter.FavoriteEpisodeRecyclerViewAdapter;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.listener.PodcastEpisodeStatusChangeHandler;
import com.qingwenwei.eslpodcaster.sqlite.SQLiteDatabaseManager;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    private static final String TAG = "FavoriteFragment";

    private RecyclerView recyclerView;
    private FavoriteEpisodeRecyclerViewAdapter adapter;
//    private SQLiteDatabaseManager db;

    public FavoriteFragment() {
        // Required empty public constructor
        this.adapter = new FavoriteEpisodeRecyclerViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
//        db = new SQLiteDatabaseManager(getContext());

        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    public void setHandler(PodcastEpisodeStatusChangeHandler handler){
        adapter.setHandler(handler);
    }

    public void refresh(){
        new GetAllFavouredEpisodesAsyncTask().execute();
    }

    private class GetAllFavouredEpisodesAsyncTask extends AsyncTask<Void, Void, ArrayList<PodcastEpisode>>{
        private static final String TAG = "GetAllFavouredEpisodesAsyncTask";
        @Override
        protected ArrayList<PodcastEpisode> doInBackground(Void... params) {
            SQLiteDatabaseManager db = new SQLiteDatabaseManager(getContext());
            ArrayList<PodcastEpisode> episodes = (ArrayList<PodcastEpisode>) db.getAllFavoriteEpisodes();
            return episodes;
        }

        @Override
        protected void onPostExecute(ArrayList<PodcastEpisode> podcastEpisodes) {
            adapter.updateEpisodes(podcastEpisodes);
            Log.i(TAG," data size:" + podcastEpisodes.size());
        }
    }

}
