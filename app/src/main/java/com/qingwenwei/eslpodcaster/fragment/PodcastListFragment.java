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
        Log.i(TAG, "PodcastListFragment:onCreateView()");

//        View rootView = inflater.inflate(R.layout.fragment_podcast_list, container, false);
//        podcastListView = (ListView)rootView.findViewById(R.id.podcast_list_view);
//        podcastListView.setOnItemClickListener(new PodcastItemOnClickListener());
//
//        if(dataInitialized) {
//            //just load podcast list when podcast info is already downloaded
//            podcastListView.setAdapter(podcastListAdapter);
//        }else {
//            //check if podcast list is initialized
//            new downloadInternetEpisodes().execute(Constants.ESLPOD_FEED_URL);
//        }


        ////////////////
        // RecyclerView
        ////////////////

        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_podcast_list, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
//        recyclerView.setHasFixedSize(true);






        if(dataInitialized) {
            //just refresh the recyclerView as the episode date was initialized
            recyclerView.setAdapter(adapter);
        }else {
            //first time download the episode data
            episodes = new ArrayList<>();
            adapter = new PodcastEpisodeRecyclerViewAdapter(null,episodes,recyclerView);
            adapter.setOnLoadMoreListener(new LoadMoreEpisodesListener());
            recyclerView.setAdapter(adapter);
            new DownloadEpisodesAsyncTask().execute(Constants.ESLPOD_ALL_EPISODE_URL);
        }

        return recyclerView;

    }

    ///////////////////////
    // load more items
    ///////////////////////
//    static class LoadingViewHolder extends RecyclerView.EpisodeViewHolder {
//        public ProgressBar progressBar;
//
//        public LoadingViewHolder(View itemView) {
//            super(itemView);
//            progressBar = (ProgressBar) itemView.findViewById(R.id.loadMoreProgressBar);
//        }
//    }



    // helpers
    private class DownloadEpisodesAsyncTask extends AsyncTask<String, Integer, ArrayList<PodcastEpisode>> {
//        private final RecyclerView recyclerView;
//        public DownloadEpisodesAsyncTask(RecyclerView recyclerView){
//            this.recyclerView = recyclerView;
//        }

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

//            adapter = new PodcastEpisodeRecyclerViewAdapter(getContext(), episodes, recyclerView);
//            recyclerView.setAdapter(adapter);

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
            adapter.setLoaded();
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





//    private InputStream downloadUrl(String urlString) throws IOException {
//        URL url = new URL(urlString);
//        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
//        conn.setReadTimeout(10000); // 10 seconds
//        conn.setConnectTimeout(15000); // 15 seconds
//        conn.setRequestMethod("GET");
//        conn.setDoInput(true);
//
//        //starts the query
//        conn.connect();
//        InputStream stream = conn.getInputStream();
//        return stream;
//    }

}
