package com.qingwenwei.eslpodcaster.entity;

import java.io.Serializable;

public class PodcastEpisode implements Serializable {
    public String title;
    public String subtitle;
    public String content;
    public String pubDate;
    public String audioFileUrl;
    public String webUrl;
    public String category;
    public boolean downloaded = false;
    public boolean favoured = false;


    public PodcastEpisode(){
        super();
    }

    public PodcastEpisode(String title, String subtitle){
        this.title = title;
        this.subtitle = subtitle;
    }

    public PodcastEpisode(String title,
                          String subtitle,
                          String content,
                          String pubDate,
                          String audioFileUrl,
                          String webUrl,
                          String category){
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.pubDate = pubDate;
        this.audioFileUrl = audioFileUrl;
        this.webUrl = webUrl;
        this.category = category;
    }

    public PodcastEpisode(String title,
                          String subtitle,
                          String content,
                          String pubDate,
                          String audioFileUrl,
                          String webUrl,
                          String category,
                          boolean downloaded,
                          boolean favoured){
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.pubDate = pubDate;
        this.audioFileUrl = audioFileUrl;
        this.webUrl = webUrl;
        this.category = category;
        this.downloaded = downloaded;
        this.favoured = favoured;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getAudioFileUrl() {
        return audioFileUrl;
    }

    public void setAudioFileUrl(String audioFileUrl) {
        this.audioFileUrl = audioFileUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isFavoured() {
        return favoured;
    }

    public void setFavoured(boolean favoured) {
        this.favoured = favoured;
    }

    public String toString(){
        return "\n\n@============================================================" +
                        "\nTitle: " + title +
                        "\ndate: " + pubDate +
                        "\nCat: " + category +
                        "\nsub: " + subtitle +
                        "\naudio: " + audioFileUrl +
                        "\nweb: " + webUrl +
                        "\ndownloaded: " + downloaded +
                        "\nfavoured: " + favoured;
    }

}
