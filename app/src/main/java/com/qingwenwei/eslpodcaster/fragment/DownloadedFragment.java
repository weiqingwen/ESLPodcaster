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
import com.qingwenwei.eslpodcaster.adapter.DownloadEpisodeRecyclerViewAdapter;

public class DownloadedFragment extends Fragment {
    private static final String TAG = "DownloadedFragment";

    private RecyclerView recyclerView;
    private DownloadEpisodeRecyclerViewAdapter adapter;

    public DownloadedFragment() {
        // Required empty public constructor
        adapter = new DownloadEpisodeRecyclerViewAdapter(null);
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

}
