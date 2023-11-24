package main;

import fileio.input.SongInput;

import java.util.ArrayList;

public class Search {
    // this class returns the first 5 songs that match the filters
    //searchSongs(library.getSongs(), username, filters);
    public static ArrayList<String> search(String type, Library library, String username, Filters filters) {
        if (type.equals("song")) {
            return searchSongs(library.getSongs(), username, filters);
        } else if (type.equals("podcast")) {
            return searchPodcasts(library.getPodcasts(), username, filters);
        } else if (type.equals("playlist")) {
           return searchPlaylist(library.getPlaylists(), username, filters);
        }
        return null;
    }
    public static ArrayList<String> searchSongs(ArrayList<Song> songs, String username, Filters filters) {
        ArrayList<String> result = new ArrayList<>();
        int count = 0;
        boolean filtru = true;
        for (Song song : songs) {
            int ceva=0;
            if (filters.getName() != null && song.getName().startsWith(filters.getName())) {
                result.add(song.getName());
                count++;
                ceva++;
            }
            if(filters.getReleaseYear() != null) {
                String filterString = filters.getReleaseYear();
                int filterYear;

                if (filterString.startsWith("<")) {
                    // Filter for release years prior to the specified year
                    filterYear = Integer.parseInt(filterString.substring(1));
                    if (Integer.parseInt(song.getReleaseYear()) < filterYear){
                        result.add(song.getName());
                        count++;
                        ceva++;
                    }
                } else if (filterString.startsWith(">")) {
                    // Filter for release years after the specified year
                    filterYear = Integer.parseInt(filterString.substring(1));
                    if (Integer.parseInt(song.getReleaseYear()) > filterYear) {
                        result.add(song.getName());
                        count++;
                        ceva++;
                        }
                    } else {
                        // Filter for release years equal to the specified year
                        filterYear = Integer.parseInt(filterString);
                        if (Integer.parseInt(song.getReleaseYear()) == filterYear) {
                            result.add(song.getName());
                            count++;
                            ceva++;
                        }
                    }
            }
            if (filters.getGenre() != null && song.getGenre().toLowerCase().contains(filters.getGenre().toLowerCase())) {
                result.add(song.getName());
                count++;
                ceva++;
            }
            if (filters.getArtist() != null && song.getArtist().equals(filters.getArtist())) {
                result.add(song.getName());
                count++;
                ceva++;
            }
            if (filters.getAlbum()!= null && song.getAlbum().contains(filters.getAlbum())) {
                result.add(song.getName());
                count++;
                ceva++;
            }
            if (filters.getLyrics()!= null && song.getLyrics().contains(filters.getLyrics())) {
                result.add(song.getName());
                count++;
                ceva++;
            }
            if (filters.getDuration()!=null && song.getDuration().equals(filters.getDuration())){
                result.add(song.getName());
                count++;
                ceva++;
            }
            //for all the tags in the filtes, check if the song contains all of them
            if (filters.getTags() != null) {
                boolean ok = true;
                for (String tag : filters.getTags()) {
                    if (!song.getTags().contains(tag)) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    result.add(song.getName());
                    count++;
                    ceva++;
                }
            }
            if (ceva >= 2){
                filtru = false;
            }
        }
        if(filtru == true){
            //returneaza primele 5 elemente din result
            ArrayList<String> result2 = new ArrayList<>();
            int limit = Math.min(5, result.size());
            for(int i = 0; i < limit; i++){
                result2.add(result.get(i));
            }
            return result2;
        }else {
            //retuneaza toate elementele din result care apar de cel putin 2 ori
            ArrayList<String> result2 = new ArrayList<>();
            for (String item : result) {
                int count2 = 0;
                for (String item2 : result) {
                    if (item.equals(item2)) {
                        count2++;
                    }
                }
                if (count2 >= 2) {
                    if(!result2.contains(item))
                        result2.add(item);
                }
            }
            return result2;
        }
    }
    public static ArrayList<String> searchPodcasts(ArrayList<Podcast> podcasts, String username, Filters filters) {
        ArrayList<String> result = new ArrayList<>();
        int count = 0;
        for (Podcast podcast : podcasts) {
            if (count == 5) {
                break;
            }
//                if (podcast.getName().contains(filters.getName())
//                        || podcast.getOwner().contains(filters.getOwner())) {
//                    result.add(podcast.getName());
//                    count++;
//                }
            if ((filters.getName() != null && podcast.getName().contains(filters.getName()))
                    || (filters.getOwner() != null && podcast.getOwner().contains(filters.getOwner()))) {
                result.add(podcast.getName());
                count++;
            }
        }
        return result;
    }
    public static ArrayList<String> searchPlaylist(ArrayList<Playlist> playList, String username, Filters filters) {
        ArrayList<String> result = new ArrayList<>();
        int count = 0;
        for (Playlist playlist : playList) {
            if (count == 5) {
                break;
            }
            if (filters.getOwner() != null && playlist.getOwner().contains(filters.getOwner())) {
                result.add(playlist.getName());
                count++;
            }
        }
        return result;
    }
}
