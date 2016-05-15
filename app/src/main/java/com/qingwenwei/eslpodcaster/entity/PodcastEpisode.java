package com.qingwenwei.eslpodcaster.entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class PodcastEpisode implements Serializable {

    private String title;
    private String subtitle;
    private String content;
    private String pubDate;
    private String audioFileUrl;
    private String webUrl;
    private String category;
    private String archived; // YES
    private String archivedDate;
    private String localAudioFile;
    private String downloadedDate;

    public PodcastEpisode(){
        super();
    }

    public PodcastEpisode(String title, String subtitle){
        this.title = title;
        this.subtitle = subtitle;
    }

    public PodcastEpisode(
            String title,
            String subtitle,
            String content,
            String pubDate,
            String audioFileUrl,
            String webUrl,
            String category,
            String localAudioFile,
            String archived){

        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.pubDate = pubDate;
        this.audioFileUrl = audioFileUrl;
        this.webUrl = webUrl;
        this.category = category;
        this.localAudioFile = localAudioFile;
        this.archived = archived;
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

    public String getLocalAudioFile() {
        return localAudioFile;
    }

    public void setLocalAudioFile(String localAudioFile) {
        this.localAudioFile = localAudioFile;
    }

    public String getArchived() {
        return archived;
    }

    public void setArchived(String archived) {
        this.archived = archived;
    }

    public String getArchivedDate() {
        return archivedDate;
    }

    public void setArchivedDate(String archivedDate) {
        this.archivedDate = archivedDate;
    }

    public String getDownloadedDate() {
        return downloadedDate;
    }

    public void setDownloadedDate(String downloadedDate) {
        this.downloadedDate = downloadedDate;
    }

    public String toString(){
        return "\n@============================================================" +
                        "\ntitle: " + title +
                        "\npub_date: " + pubDate +
                        "\ncategory: " + category +
                        "\nsubtitle: " + subtitle +
                        "\naudio_url: " + audioFileUrl +
                        "\nweb_url: " + webUrl +
                        "\nlocal_audio: " + localAudioFile;
    }

    public static String currentDateString(){
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(now);
    }

    public static Comparator<PodcastEpisode> getComparatorByArchivedDate(){
        Comparator comparator = new Comparator<PodcastEpisode>(){
            @Override
            public int compare(PodcastEpisode ep1, PodcastEpisode ep2) {
                String downloadedDateString1 = ep1.getArchivedDate();
                String downloadedDateString2 = ep2.getArchivedDate();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date downloadedDate1 = null;
                Date downloadedDate2 = null;
                try {
                    downloadedDate1 = dateFormat.parse(downloadedDateString1);
                    downloadedDate2 = dateFormat.parse(downloadedDateString2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return downloadedDate1.compareTo(downloadedDate2);
            }
        };
        return comparator;
    }

    public static Comparator<PodcastEpisode> getComparatorByDownloadedDate(){
        Comparator comparator = new Comparator<PodcastEpisode>(){
            @Override
            public int compare(PodcastEpisode ep1, PodcastEpisode ep2) {
                String downloadedDateString1 = ep1.getDownloadedDate();
                String downloadedDateString2 = ep2.getDownloadedDate();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date downloadedDate1 = null;
                Date downloadedDate2 = null;
                try {
                    downloadedDate1 = dateFormat.parse(downloadedDateString1);
                    downloadedDate2 = dateFormat.parse(downloadedDateString2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return downloadedDate1.compareTo(downloadedDate2);
            }
        };
        return comparator;
    }

//    public static void main(String[] args) throws InterruptedException {
//        Date early = new Date();
//        Thread.sleep(2000);
//        Date late = new Date();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        String earlyString = format.format(early);
//        String lateString = format.format(late);
//
////        System.out.println("early: " + earlyString);
////        System.out.println("late: " + lateString);
//
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        try {
//            Date newEarly = dateFormat.parse(earlyString);
//            Date newLate = dateFormat.parse(lateString);
//            System.out.println(newEarly.compareTo(newLate));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

}
