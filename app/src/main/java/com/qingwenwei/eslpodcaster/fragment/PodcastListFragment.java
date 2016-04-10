package com.qingwenwei.eslpodcaster.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.qingwenwei.eslpodcaster.R;
import com.qingwenwei.eslpodcaster.adapter.PodcastListViewAdapter;
import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.entity.PodcastItem;
import com.qingwenwei.eslpodcaster.util.XmlParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class PodcastListFragment extends Fragment {
    private static String TAG = "@[PodcastListFragment]";
    private boolean isDownloaded = false;
    private ListView podcastListView;
    private PodcastListViewAdapter podcastListAdapter;

    public PodcastListFragment(){
        Log.i(TAG,"PodcastListFragment()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "PodcastListFragment:onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "PodcastListFragment:onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_podcast_list, container, false);
        podcastListView = (ListView)rootView.findViewById(R.id.podcast_list_view);

        if(isDownloaded) {
            //just load podcast list when podcast info is already downloaded
            podcastListView.setAdapter(podcastListAdapter);
        }else {
            //check if podcast list is initialized
            new downloadXmlTask().execute(Constants.ESLPOD_FEED_URL);
        }

        return rootView;
    }


    // helpers
    private class downloadXmlTask extends AsyncTask<String, Integer, ArrayList<PodcastItem>> {

        @Override
        protected ArrayList<PodcastItem> doInBackground(String... urls) {
            InputStream stream;
            ArrayList result = null;
            try {
                stream = downloadUrl(urls[0]);
                result = (ArrayList) new XmlParser().parse(stream);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<PodcastItem> result) {
            Log.i(TAG, "onPostExecute()  size:" + result.size());

//            for(PodcastItem i: result){
//                Log.i(TAG,""+i);
//            }

            podcastListAdapter = new PodcastListViewAdapter(getContext(), R.layout.podcast_row_layout, result);
            podcastListView.setAdapter(podcastListAdapter);
            isDownloaded = true;
        }

    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000); // 10 seconds
        conn.setConnectTimeout(15000); // 15 seconds
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        //starts the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
    }
}
