package main;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class Library {
    private ArrayList<Podcast> podcasts;
    private ArrayList<Song> songs;
    private ArrayList<User> users;
    private ArrayList<Playlist> playlists;
    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
    }
}
@Getter
@Setter
class Playlist extends Library{
    private ArrayList<Song> piese;
    private String owner;
    private int id;
    private String name;
    private int followers;
    private String visibilty;
}
