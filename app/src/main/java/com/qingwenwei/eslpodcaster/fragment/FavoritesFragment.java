package com.qingwenwei.eslpodcaster.fragment;


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

public class FavoritesFragment extends Fragment {
    private static final String TAG = "FavoritesFragment";

    private RecyclerView recyclerView;
    private FavoriteEpisodeRecyclerViewAdapter adapter;


    public FavoritesFragment() {
        // Required empty public constructor
        adapter = new FavoriteEpisodeRecyclerViewAdapter(null);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_favorites, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
