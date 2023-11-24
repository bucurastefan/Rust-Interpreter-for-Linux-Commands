package main;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Status  {
    private String name;
    private int timestamp;
    private String repeat;
    private Boolean playing;
    private Boolean shuffle;
    private Boolean song_podcast;
    private int remainedTime;
    private int duration;
    //calculam timpul ramas
    public void loadsong(String songName, int timestamp, ArrayList<Song> songs,Status status) {
        status.name = songName;
        for(Song song:songs){
            if(song.getName().equals(songName)){
                status.duration=song.getDuration();
                break;
            }
        }
        status.timestamp = timestamp;
        status.remainedTime = duration;
        status.playing=true;
        status.shuffle=false;
        status.repeat="No Repeat";
    }
    public void loadpodcast(String podcastName, int timestamp, ArrayList<Podcast> podcasts,User user) {
        if(user.getPodcasts()!=null){
            for(Podcast podcast:user.getPodcasts()){
                if(podcast.getName().equals(podcastName)){
                    for(Episode episode:podcast.getEpisodes()){
                        if(episode.getIswatched()==null){
                            this.name = podcastName;
                            this.duration=episode.getDuration();
                            this.timestamp = timestamp;
                            this.remainedTime = duration;
                            this.playing=true;
                            episode.setIswatched(false);
                            episode.setCurentplaytime(remainedTime);
                            break;
                        }else if(episode.getIswatched()==false){
                            this.name = podcastName;
                            this.duration=episode.getDuration();
                            this.timestamp = timestamp;
                            this.remainedTime = episode.getCurentplaytime();
                            this.playing=true;
                            break;
                        }
                    }
                }
            }
        }
        else{
            for(Podcast podcast:podcasts){
                if(podcast.getName().equals(podcastName)){
                    // we add it to the user's podcasts
                    if(user.getPodcasts()==null){
                        ArrayList<Podcast> userPodcasts = new ArrayList<>();
                        userPodcasts.add(podcast);
                        user.setPodcasts(userPodcasts);
                    }
                    else{
                        user.getPodcasts().add(podcast);
                    }
                    // we add just the first episode
                    for(Episode episode:podcast.getEpisodes()){
                            this.name = podcastName;
                            this.duration=episode.getDuration();
                            this.timestamp = timestamp;
                            this.remainedTime = duration;
                            this.playing=true;
                            break;
                    }
                }
            }
        }
    }
    public void PlayPause(int timestamp){
        if(this.playing){
            this.playing=false;
            this.remainedTime=this.remainedTime-(timestamp-this.timestamp);
        }
        else{
            this.playing=true;
            this.timestamp=timestamp;
        }
    }
    public void Stats(int timestamp){
        if(this.playing){
            this.remainedTime=this.remainedTime-(timestamp-this.timestamp);
        }

    }
    public static Boolean getSongPodcast(String Name, ArrayList<Song> songs, ArrayList<Podcast> podcasts){
        for(Song song:songs){
            if(song.getName().equals(Name)){
                return true;
            }
        }
        for(Podcast podcast:podcasts){
            if(podcast.getName().equals(Name)){
                return false;
            }
        }
        return null;
    }
    public void updateStatus(Status stats, int timestamp){
        if(stats.getPlaying()){
            stats.setRemainedTime(stats.getRemainedTime()-(timestamp-stats.getTimestamp()));
            if(stats.getRemainedTime()<=0){
                stats.setPlaying(false);
                stats.setRemainedTime(0);
                stats.setName("");
            }
        }
        stats.setTimestamp(timestamp);
    }
}
