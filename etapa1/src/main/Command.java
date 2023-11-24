package main;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class Command {
    private String command;
    private String username;
    private int timestamp;
    private String type;
    private Filters filters;
    private int itemNumber;
    private String playlistName;
    private int playlistId;
    private int seed;
}
@Getter
@Setter
class Filters {
    private String name;
    private Integer duration;
    private String album;
    private ArrayList<String> tags;
    private String lyrics;
    private String genre;
    private String releaseYear;
    private String artist;
    private String owner;
}

