package com.qingwenwei.eslpodcaster.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qingwenwei.eslpodcaster.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadedFragment extends Fragment {


    public DownloadedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the player_panel_layout for this fragment
        return inflater.inflate(R.layout.fragment_downloaded, container, false);
    }

}
