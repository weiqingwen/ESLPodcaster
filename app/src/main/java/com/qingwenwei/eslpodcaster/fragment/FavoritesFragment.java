package com.qingwenwei.eslpodcaster.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qingwenwei.eslpodcaster.R;

public class FavoritesFragment extends Fragment {
    private static final String TAG = "FavoritesFragment";

    private RecyclerView recyclerView;


    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");
        recyclerView = (RecyclerView)container.findViewById(R.id.podcastRecyclerView);


        // Inflate the panel_layout_sliding_up_player for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

}
