package com.qingwenwei.eslpodcaster.fragment;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.adapter.DownloadListAdapter;
import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.db.EpisodeDAO;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;
import com.qingwenwei.eslpodcaster.event.OnEpisodeListRefreshEvent;
import com.qingwenwei.eslpodcaster.event.OnLoadPlayingEpisodeEvent;
import com.qingwenwei.eslpodcaster.util.EpisodeStatusUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class DownloadListFragment extends Fragment
        implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "DownloadListFragment";

    private RecyclerView recyclerView;
    private DownloadListAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");

        adapter = new DownloadListAdapter();
        adapter.setOnCardViewClickListener(this);
        adapter.setOnCardViewLongClickListener(this);

        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_downloads, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.downloadCardView:
                showPopupMenu((PodcastEpisode) v.getTag());
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        //load playing episode
        switch (v.getId()){
            case R.id.downloadCardView:
                PodcastEpisode episode = (PodcastEpisode) v.getTag();
                EventBus.getDefault().post(new OnLoadPlayingEpisodeEvent(episode));
                break;
        }
    }

    @Subscribe
    public void refresh(OnEpisodeListRefreshEvent event){
        if(event.message.equals(Constants.ON_DOWNLOADED_EPISODE_LIST_REFRESH_EVENT))
            new RefreshDownloadedEpisodesListAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class RefreshDownloadedEpisodesListAsyncTask extends AsyncTask<Void, Void, ArrayList<PodcastEpisode>> {
        private static final String TAG = "RefreshDownloadedEpisodesListAsyncTask";
        @Override
        protected ArrayList<PodcastEpisode> doInBackground(Void... params) {
            EpisodeDAO dao = new EpisodeDAO(getContext());
            ArrayList downloads = (ArrayList) dao.getAllDownloadedEpisodes();
            return downloads;
        }

        @Override
        protected void onPostExecute(ArrayList<PodcastEpisode> podcastEpisodes) {
            Log.i(TAG,"Refreshed download list size: " + podcastEpisodes.size());
            if (podcastEpisodes.size() == 0) podcastEpisodes.add(null);
            adapter.updateEpisodes(podcastEpisodes);

        }
    }

    private void showPopupMenu(final PodcastEpisode episode){
        CharSequence items[] = new CharSequence[] {
                "Archive this episode",
                "Delete this episode"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG,episode.getTitle() + " which:" + which);
                switch (which){
                    //archive
                    case 0:{
                        EpisodeStatusUtil.archiveEpisode(episode, getContext());
                        break;
                    }

                    //delete
                    case 1:{
                        EpisodeStatusUtil.deleteEpisode(episode, getContext());
                        EventBus.getDefault().post(
                                new OnEpisodeListRefreshEvent(
                                        Constants.ON_DOWNLOADED_EPISODE_LIST_REFRESH_EVENT));
                        break;
                    }
                }
            }
        });
        builder.show();
    }

}
