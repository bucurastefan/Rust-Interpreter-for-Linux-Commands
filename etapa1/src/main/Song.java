package main;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class Song {
        private String name;
        private Integer duration;
        private String album;
        private ArrayList<String> tags;
        private String lyrics;
        private String genre;
        private String releaseYear;
        private String artist;
}

