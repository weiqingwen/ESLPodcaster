package com.qingwenwei.eslpodcaster.util;

import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class RoundTableEpScriptParser {
    private final static String TAG = "RoundTableEpScriptParser";

    //passing an episode as reference
    public void getEpisodeScript(PodcastEpisode episode){
        try {
            Document doc = Jsoup.connect(Constants.ROUND_TABLE_WEB_PAGE_BASE_URL + episode.getWebUrl()).get();
            Elements pods = doc.select("div#ccontent p");

            StringBuilder content = new StringBuilder();
            for(Element pod : pods){
                if(pod.html().contains("<strong>")){
                    content.append(pod.html() + "<br><br>");
                }else {
                    content.append(pod.text() + "<br><br>");
                }
            }

//            for(Element pod : pods){
//                if (pod.text().contains("Audio Index:")){
//                    String audioIndex = pod.html();
//                    if(audioIndex.toLowerCase().contains("slow dialog"))
//                        content += audioIndex + "<br>";
//                }else{
//                    content += pod.html();
//                }
//            }

            episode.setContent(content.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String args[]){
        PodcastEpisode episode = new PodcastEpisode();
        episode.setWebUrl("/4926/2017/04/20/2361s953136.htm");
        new RoundTableEpScriptParser().getEpisodeScript(episode);
    }
}
