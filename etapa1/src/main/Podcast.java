package main;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@Getter
@Setter
public class Podcast {
    private String name;
    private String owner;
    private ArrayList<Episode> episodes;
}
