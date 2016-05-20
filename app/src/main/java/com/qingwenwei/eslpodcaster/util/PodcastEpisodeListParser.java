package com.qingwenwei.eslpodcaster.util;

import android.util.Log;

import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PodcastEpisodeListParser {
    private final static String TAG = "PodcastEpisodeListParser";

    public List<PodcastEpisode> parseEpisodes(String url){
        List<PodcastEpisode> episodes = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect(url).timeout(5000).get();

            Elements pods = doc.select("table.podcast_table_home:has(span.date-header)");
            System.out.println("pod size: " + pods.size());

            //all podcast episodes
            for(Element pod : pods){
                String title = pod.select("a[class=podcast_title]").text();
                String cat = pod.select("a[href*=show_all.php?cat_id=]").text();
                String web = pod.select("a[href*=show_podcast.php?issue_id=]").attr("href");
                String date = pod.select("span.date-header").text();
                String file = pod.select("a[href$=.mp3]").attr("href");

                int start = pod.text().indexOf("Download Podcast ");
                int end = pod.text().lastIndexOf("Tags:");
                String subTitle= pod.text().substring(17 + start, end);

                PodcastEpisode newEpisode = new PodcastEpisode(
                        title,
                        subTitle,
                        "", //content
                        date,
                        file,
                        web,
                        cat,
                        "", //local audio file
                        ""  //archived
                );

                episodes.add(newEpisode);
            }

        } catch (IOException e) {
//            e.printStackTrace();
            Log.i(TAG, "Failed to load podcast list");
        }
        return episodes;
    }

//    public static void main(String args[]){
//        new PodcastEpisodeListParser().parseEpisodes("http://www.eslpod.com/website/show_all.php?low_rec=0");
//    }
}

