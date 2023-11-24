package main;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public final class User {
    private String username;
    private int age;
    private String city;
    private List<String> lastSearch;
    private String loadedFile;
    private Boolean Play;
    private ArrayList<Podcast> podcasts;
    private ArrayList<Song> likedSongs;
}
