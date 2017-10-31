package com.qingwenwei.eslpodcaster.util;


import android.util.Log;

import com.qingwenwei.eslpodcaster.constant.Constants;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class RoundTableEpListParser {
    private final static String TAG = "RoundTableEpListParser";
//    private final static String baseURL = "http://english.cri.cn/";     //base URL

    public List<PodcastEpisode> parseEpisodes(String url){
        List<PodcastEpisode> episodes = new ArrayList<>();
        try {

            Document doc = Jsoup.connect(url).timeout(5000).get();
            Elements rows = doc.select("td.pad td");
            System.out.println("number of episodes: " + rows.size());

            Pattern p = Pattern.compile("-");
            for(Element ep : rows){
                //extract title and description
                String epRawText = ep.select("a[href]").text(); //raw text to modify
                String[] titleAndSubtitle = p.split(epRawText);
                String title = titleAndSubtitle[0].trim();      //title
                String subtitle = titleAndSubtitle[1].trim();   //subtitle

                //extract full page url
                String web = ep.select("a[href]").attr("href"); //full web page URL

                //create a new episode object to add to list
                PodcastEpisode newEpisode = new PodcastEpisode(
                        title,      //title
                        subtitle,   //subtitle
                        "",         //content
                        "",         //date
                        "",         //mp3 file URL
                        web,        //episode web page URL (partial url)
                        randomCardViewIcon(),     //category
                        "",         //local audio file
                        ""          //archived marker(if archived or not)
                );
                episodes.add(newEpisode);
            }

        } catch (IOException e) {
            Log.i(TAG, "Failed to load podcast list");
        }

        //start threads to extract information from each episode's web page
        List<Thread> threads = new ArrayList<>();
        for (PodcastEpisode ep : episodes){
            Thread thread = new ExtractWebThread(ep);
            threads.add(thread);
            thread.start();
        }

        //wait for all threads terminated
        while(true) {
            int terminated = 0;
            for (Thread thread : threads) {
                if (thread.getState() == Thread.State.TERMINATED)
                    terminated++;
            }
            if(terminated == 40)    //40 - number of episodes on a page
                break;
        }

        //return all episodes when all threads terminated
        return episodes;
    }


    /*
    A task for extracting web page elements such as date, episode content, mp3
     */
    static class ExtractWebThread extends Thread{
        PodcastEpisode episode;
        public ExtractWebThread(PodcastEpisode episode){
            this.episode = episode;
        }

        public void run(){
            String fullWebURL = Constants.ROUND_TABLE_WEB_PAGE_BASE_URL + episode.getWebUrl();
            try {
                Document doc = Jsoup.connect(fullWebURL).timeout(5000).get();
                //extract audio URL
                String mp3Url = doc.select("a[href$=.mp3]").attr("href");
                episode.setAudioFileUrl(mp3Url);

                //extract date
                String date = doc.select("div.content02").text().replaceAll("\\u00a0","").substring(0,10);
                episode.setPubDate(date);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static String randomCardViewIcon(){
        int max = 10;
        int min = 1;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        String iconTag;
        switch (randomNum){
            case 1: iconTag = "relationships"; break;
            case 2: iconTag = "dining"; break;
            case 3: iconTag = "english caf"; break;
            case 4: iconTag = "daily life"; break;
            case 5: iconTag = "shopping"; break;
            case 6: iconTag = "health/medicine"; break;
            case 7: iconTag = "travel"; break;
            case 8: iconTag = "transportation"; break;
            case 9: iconTag = "business"; break;
            case 10: iconTag = "entertainment"; break;
            default: iconTag = "travel";
        }
        return iconTag;
    }

    //test code
    public static void main(String args[]){
        List<PodcastEpisode> episodes = new RoundTableEpListParser().parseEpisodes("http://english.cri.cn/4926/more/11680/more11680.htm");

        for (PodcastEpisode ep : episodes){
            System.out.println(ep);
        }
    }

}
