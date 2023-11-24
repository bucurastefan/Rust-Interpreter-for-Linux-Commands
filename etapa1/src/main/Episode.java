package main;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Episode {
    private String name;
    private Integer duration;
    private String description;
    private Integer curentplaytime;
    private Boolean iswatched;

}
