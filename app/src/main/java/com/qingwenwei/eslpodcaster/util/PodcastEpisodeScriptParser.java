package com.qingwenwei.eslpodcaster.util;

import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class PodcastEpisodeScriptParser {
    private final static String TAG = "PodcastEpisodeListParser";

    public void getEpisodeScript(PodcastEpisode episode){
        Document doc;
        try {
            doc = Jsoup.connect(Constants.ESLPOD_BASE_EPISODE_URL + "/" + episode.getWebUrl()).get();
            Elements pods = doc.select("table.podcast_table_home:has(span.pod_body)");
            for(Element pod : pods){
                if (pod.text().contains("Audio Index:")){

                }else{
                    episode.setContent(pod.html());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
