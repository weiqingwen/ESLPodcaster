package com.qingwenwei.eslpodcaster.util;

import android.content.Context;
import android.widget.Toast;

import com.qingwenwei.eslpodcaster.db.EpisodeDAO;
import com.qingwenwei.eslpodcaster.entity.PodcastEpisode;

public class EpisodeStatusUtil {
    private static final String TAG = "EpisodeStatusUtil";

    public static void archiveEpisode(PodcastEpisode episode, Context context) {
        EpisodeDAO dao = new EpisodeDAO(context);

        if(dao.isArchived(episode)) {
            //episode is already archived
            Toast.makeText(context, "Already archived", Toast.LENGTH_LONG).show();

        }else if(dao.isDownloaded(episode)) {
            //just update the status from the downloaded episode
            PodcastEpisode oldEpisode = dao.getEpisode(episode);

            //setup archived tag and archived date
            oldEpisode.setArchived("YES");
            oldEpisode.setArchivedDate(PodcastEpisode.currentDateString());

            //update the pre-existent episode
            dao.updateEpisode(oldEpisode);
            Toast.makeText(context, "Archived " + oldEpisode.getTitle(), Toast.LENGTH_LONG).show();

        }else{
            //episode has not been stored yet
            //setup archived tag and archived date
            episode.setArchived("YES");
            episode.setArchivedDate(PodcastEpisode.currentDateString());

            //create a new episode database entry
            long i = dao.createEpisode(episode);
            Toast.makeText(context, "Archived " + episode.getTitle(), Toast.LENGTH_LONG).show();

            //error occurs
            if(i == -1){
                Toast.makeText(context, "Failed to archive episode.", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    public static void unarchiveEpisode(PodcastEpisode episode, Context context){
        EpisodeDAO dao = new EpisodeDAO(context);

        if(dao.isArchived(episode)){
            if(dao.isDownloaded(episode)){
                PodcastEpisode oldEpisode = dao.getEpisode(episode);
                oldEpisode.setArchived(null);
                oldEpisode.setArchivedDate(null);
                dao.updateEpisode(oldEpisode);
                Toast.makeText(context, "Unarchived " + oldEpisode.getTitle(), Toast.LENGTH_LONG).show();
            }

            if(!dao.isDownloaded(episode)){
                dao.deleteEpisode(episode);
                Toast.makeText(context, "Unarchived " + episode.getTitle(), Toast.LENGTH_LONG).show();
            }
        }

    }

    public static void downloadEpisode(PodcastEpisode episode, Context context){
        new EpisodeDownloader(context).startDownload(episode);
    }

    public static void deleteEpisode(PodcastEpisode episode, Context context){
        EpisodeDAO dao = new EpisodeDAO(context);
        if(dao.isDownloaded(episode)){
            if(dao.isArchived(episode)){
                PodcastEpisode oldEpisode = dao.getEpisode(episode);
                //delete the local audio file
                if(FileUtil.deleteFile(oldEpisode.getLocalAudioFile()))
                    Toast.makeText(context, "Deleted " + oldEpisode.getTitle(), Toast.LENGTH_LONG).show();

                oldEpisode.setLocalAudioFile(null);
                oldEpisode.setDownloadedDate(null);
                dao.updateEpisode(oldEpisode);
            }

            if(!dao.isArchived(episode)){
                //delete the local audio file
                if(FileUtil.deleteFile(episode.getLocalAudioFile()))
                    Toast.makeText(context, "Deleted " + episode.getTitle(), Toast.LENGTH_LONG).show();

                dao.deleteEpisode(episode);
            }
        }
    }
}
