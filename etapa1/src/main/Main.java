package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePathInput for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
                              final String filePathOutput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        ArrayNode outputs = objectMapper.createArrayNode();


        // TODO add your implementation
        ObjectMapper mapper1= new ObjectMapper();
        Library librarytrue = objectMapper.readValue(new File(LIBRARY_PATH), Library.class);

        ObjectMapper mapper = new ObjectMapper();
        Command[] commands = mapper.readValue(new File(CheckerConstants.TESTS_PATH + filePathInput), Command[].class);

        Status status = new Status();

        ArrayList<Playlist> playlists = new ArrayList<>();
        librarytrue.setPlaylists(playlists);

        for (Command command : commands) {
            String commandType = command.getCommand();
            String username = command.getUsername();
            int timestamp = command.getTimestamp();

            switch (commandType) {
                case "search":
                    Search search = new Search();
                    String type = command.getType();
                    List<String> result = Search.search(type, librarytrue, username, command.getFilters());
                    ObjectMapper objectMappersearch = new ObjectMapper();
                    //set the users last search
                    for (User user : librarytrue.getUsers()) {
                        if (user.getUsername().equals(username)) {
                            user.setLastSearch(result);
                        }
                    }

                    // Create ObjectNode
                    ObjectNode searchResult = objectMappersearch.createObjectNode();

                    // Add fields to ObjectNode
                    searchResult.put("command", commandType);
                    searchResult.put("user", username);
                    searchResult.put("timestamp", timestamp);
                    searchResult.put("message", "Search returned " + result.size() + " results");

                    // Create ArrayNode for results
                    ArrayNode resultsArray = objectMappersearch.createArrayNode();
                    for (String item : result) {
                        resultsArray.add(item);
                    }

                    // Add results ArrayNode to ObjectNode
                    searchResult.set("results", resultsArray);
                    outputs.add(searchResult);

                    break;
                case "select":
                    int itemNumber = command.getItemNumber();
                    //if a last search was made by the user and the itemNumber is valid then select the song
                    for (User user : librarytrue.getUsers()) {
                        if (user.getUsername().equals(username)) {
                            if (user.getLastSearch() != null && itemNumber <= user.getLastSearch().size()) {
                                String songName = user.getLastSearch().get(itemNumber - 1);
                                user.setLoadedFile(songName);
                                ObjectMapper objectMapperselect = new ObjectMapper();
                                // Create ObjectNode
                                ObjectNode selectResult = objectMapperselect.createObjectNode();

                                // Add fields to ObjectNode
                                selectResult.put("command", commandType);
                                selectResult.put("user", username);
                                selectResult.put("timestamp", timestamp);
                                selectResult.put("message", "Successfully selected "+ songName+".");

                                outputs.add(selectResult);
                                break;
                            } else if (user.getLastSearch()!=null) {
                                ObjectMapper objectMapperselect = new ObjectMapper();
                                // Create ObjectNode
                                ObjectNode selectResult = objectMapperselect.createObjectNode();

                                // Add fields to ObjectNode
                                selectResult.put("command", commandType);
                                selectResult.put("user", username);
                                selectResult.put("timestamp", timestamp);
                                selectResult.put("message", "The selected ID is too high.");

                                outputs.add(selectResult);
                                break;
                            } else {
                                ObjectMapper objectMapperselect = new ObjectMapper();
                                // Create ObjectNode
                                ObjectNode selectResult = objectMapperselect.createObjectNode();

                                // Add fields to ObjectNode
                                selectResult.put("command", commandType);
                                selectResult.put("user", username);
                                selectResult.put("timestamp", timestamp);
                                selectResult.put("message", "Please conduct a search before making a selection.");

                                outputs.add(selectResult);
                                break;
                            }
                        }
                    }
                    break;
                case "load":
                    for (User user : librarytrue.getUsers()) {
                        if (user.getUsername().equals(username) && user.getLoadedFile() != null) {
                           String Name=user.getLoadedFile();
                           // we need to know if it is a podcast or a song
                            if(Boolean.TRUE.equals(Status.getSongPodcast(Name, librarytrue.getSongs(), librarytrue.getPodcasts())))
                            {
                                status.loadsong(Name, timestamp, librarytrue.getSongs(), status);
                                ObjectMapper objectMapperload = new ObjectMapper();
                                // Create ObjectNode
                                ObjectNode loadResult = objectMapperload.createObjectNode();
                                loadResult.put("command", commandType);
                                loadResult.put("user", username);
                                loadResult.put("timestamp", timestamp);
                                loadResult.put("message", "Playback loaded successfully.");

                                outputs.add(loadResult);
                                break;
                            }else {
                                status.loadpodcast(Name,timestamp,librarytrue.getPodcasts(),user);
                                ObjectMapper objectMapperload = new ObjectMapper();
                                // Create ObjectNode
                                ObjectNode loadResult = objectMapperload.createObjectNode();
                                loadResult.put("command", commandType);
                                loadResult.put("user", username);
                                loadResult.put("timestamp", timestamp);
                                loadResult.put("message", "Playback loaded successfully.");

                                outputs.add(loadResult);
                                break;
                            }
                        }

                    }
                    break;
                case "status":
                    status.updateStatus(status,timestamp);
                    ObjectMapper objectMapperstatus = new ObjectMapper();
                    // Create ObjectNode
                    ObjectNode statusResult = objectMapperstatus.createObjectNode();
                    statusResult.put("command", commandType);
                    statusResult.put("user", username);
                    statusResult.put("timestamp", timestamp);
                    ObjectNode statusObject = objectMapperstatus.createObjectNode();
                    statusObject.put("name", status.getName());
                    statusObject.put("remainedTime", status.getRemainedTime());
                    statusObject.put("repeat", status.getRepeat());
                    statusObject.put("shuffle", status.getShuffle());
                    boolean playing = status.getPlaying();
                    //change the boolean if it is true to false and vice versa
                    if(playing){
                        playing=false;
                    }else{
                        playing=true;
                    }
                    statusObject.put("paused", playing);
                    statusResult.set("stats", statusObject);
                    outputs.add(statusResult);
                    break;
                case "playPause":
                    status.PlayPause(timestamp);
                    ObjectMapper objectMapperplayPause = new ObjectMapper();
                    // Create ObjectNode
                    ObjectNode playPauseResult = objectMapperplayPause.createObjectNode();
                    playPauseResult.put("command", commandType);
                    playPauseResult.put("user", username);
                    playPauseResult.put("timestamp", timestamp);
                    if(status.getPlaying()){
                        playPauseResult.put("message", "Playback resumed successfully.");
                    }else{
                        playPauseResult.put("message", "Playback paused successfully.");
                    }
                    outputs.add(playPauseResult);
                    break;
                case "createPlaylist":
                    String playlistName = command.getPlaylistName();
                    //we need to check if the playlist already exists for this user
                    boolean exists=false;
                    for (Playlist playlist : playlists) {
                        if (playlist.getName().equals(playlistName) && playlist.getOwner().equals(username)) {
                            exists = true;
                            break;
                        }
                    }

                    if (exists==false) {
                        //adaugam playlistul
                        Playlist playlist = new Playlist();
                        playlist.setName(playlistName);
                        playlist.setOwner(username);
                        playlist.setId(librarytrue.getPlaylists().size()+1);
                        playlist.setFollowers(0);
                        playlist.setVisibilty("public");
                        playlist.setSongs(new ArrayList<>());
                        librarytrue.addPlaylist(playlist);
                        ObjectMapper objectMappercreatePlaylist = new ObjectMapper();
                        // Create ObjectNode
                        ObjectNode createPlaylistResult = objectMappercreatePlaylist.createObjectNode();
                        createPlaylistResult.put("command", commandType);
                        createPlaylistResult.put("user", username);
                        createPlaylistResult.put("timestamp", timestamp);
                        createPlaylistResult.put("message", "Playlist created successfully.");
                        outputs.add(createPlaylistResult);
                        
                    } else {
                        ObjectMapper objectMappercreatePlaylist = new ObjectMapper();
                        // Create ObjectNode
                        ObjectNode createPlaylistResult = objectMappercreatePlaylist.createObjectNode();
                        createPlaylistResult.put("command", commandType);
                        createPlaylistResult.put("user", username);
                        createPlaylistResult.put("timestamp", timestamp);
                        createPlaylistResult.put("message", "Playlist already exists.");
                        outputs.add(createPlaylistResult);
                        break;
                    }
                    break;
                case "addRemoveInPlaylist":
                    // if current user has a song loaded
                    for(User user:librarytrue.getUsers()) {
                        if (user.getUsername().equals(username) && user.getLoadedFile() != null
                                && Boolean.TRUE.equals(Status.getSongPodcast(user.getLoadedFile(), librarytrue.getSongs(), librarytrue.getPodcasts()))) {
                            String songName = user.getLoadedFile();
                            int id = command.getPlaylistId();
                            // we need to check if the playlist id matches the id of the playlist we want to add the song to
                            for (Playlist playlist : librarytrue.getPlaylists()) {
                                if (playlist.getId() == id) {
                                    // we need to check if the song is already in the playlist
                                    boolean ok = false;
                                    if(playlist.getPiese()==null){
                                        ArrayList<Song> piese = new ArrayList<>();
                                        playlist.setPiese(piese);
                                    }
                                    for (Song song : playlist.getPiese()) {
                                        if (song.getName().equals(songName)) {
                                            ok = true;
                                            break;
                                        }
                                    }
                                    if (ok == false) {
                                        // we add the song to the playlist
                                        for (Song song : librarytrue.getSongs()) {
                                            if (song.getName().equals(songName)) {
                                                playlist.getPiese().add(song);
                                                ObjectMapper objectMapperaddRemoveInPlaylist = new ObjectMapper();
                                                // Create ObjectNode
                                                ObjectNode addRemoveInPlaylistResult = objectMapperaddRemoveInPlaylist.createObjectNode();
                                                addRemoveInPlaylistResult.put("command", commandType);
                                                addRemoveInPlaylistResult.put("user", username);
                                                addRemoveInPlaylistResult.put("timestamp", timestamp);
                                                addRemoveInPlaylistResult.put("message", "Successfully added to playlist.");
                                                outputs.add(addRemoveInPlaylistResult);
                                                break;
                                            }
                                        }
                                    } else {
                                        // we remove the song from the playlist
                                        for (Song song : librarytrue.getSongs()) {
                                            if (song.getName().equals(songName)) {
                                                playlist.getPiese().remove(song);
                                                ObjectMapper objectMapperaddRemoveInPlaylist = new ObjectMapper();
                                                // Create ObjectNode
                                                ObjectNode addRemoveInPlaylistResult = objectMapperaddRemoveInPlaylist.createObjectNode();
                                                addRemoveInPlaylistResult.put("command", commandType);
                                                addRemoveInPlaylistResult.put("user", username);
                                                addRemoveInPlaylistResult.put("timestamp", timestamp);
                                                addRemoveInPlaylistResult.put("message", "Successfully removed from playlist.");
                                                outputs.add(addRemoveInPlaylistResult);
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                    }
                    break;
                case "like":
                    // if current user has a song loaded
                    for(User user:librarytrue.getUsers()) {
                        if (user.getUsername().equals(username) && user.getLoadedFile() != null
                                && Boolean.TRUE.equals(Status.getSongPodcast(user.getLoadedFile(), librarytrue.getSongs(), librarytrue.getPodcasts()))) {
                            String songName = user.getLoadedFile();
                            // we need to check if the song is already in the user's liked songs
                            boolean ok = false;
                            if (user.getLikedSongs() == null) {
                                ArrayList<Song> likedSongs = new ArrayList<>();
                                user.setLikedSongs(likedSongs);
                            }
                            for (Song song : user.getLikedSongs()) {
                                if (song.getName().equals(songName)) {
                                    ok = true;
                                    break;
                                }
                            }
                            if (ok == false) {
                                // we add the song to the user's liked songs
                                for (Song song : librarytrue.getSongs()) {
                                    if (song.getName().equals(songName)) {
                                        user.getLikedSongs().add(song);
                                        ObjectMapper objectMapperlike = new ObjectMapper();
                                        // Create ObjectNode
                                        ObjectNode likeResult = objectMapperlike.createObjectNode();
                                        likeResult.put("command", commandType);
                                        likeResult.put("user", username);
                                        likeResult.put("timestamp", timestamp);
                                        likeResult.put("message", "Like registered successfully.");
                                        outputs.add(likeResult);
                                        break;
                                    }
                                }
                            } else {
                                // we remove the song from the user's liked songs
                                for (Song song : librarytrue.getSongs()) {
                                    if (song.getName().equals(songName)) {
                                        user.getLikedSongs().remove(song);
                                        ObjectMapper objectMapperlike = new ObjectMapper();
                                        // Create ObjectNode
                                        ObjectNode likeResult = objectMapperlike.createObjectNode();
                                        likeResult.put("command", commandType);
                                        likeResult.put("user", username);
                                        likeResult.put("timestamp", timestamp);
                                        likeResult.put("message", "Unlike registered successfully.");
                                        outputs.add(likeResult);
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                    }
                    break;
                case "showPlaylists":
                    // for current user we print all his playlists and song names in them and the name of the playlist and
                    // the number of followers for each playlist and the visibility of the playlist
                    for(User user:librarytrue.getUsers()) {
                        if (user.getUsername().equals(username)) {
                            ObjectMapper objectMappershowPlaylist = new ObjectMapper();
                            // Create ObjectNode
                            ObjectNode showPlaylistResult = objectMappershowPlaylist.createObjectNode();
                            showPlaylistResult.put("command", commandType);
                            showPlaylistResult.put("user", username);
                            showPlaylistResult.put("timestamp", timestamp);
                            ArrayNode playlistsArray = objectMappershowPlaylist.createArrayNode();
                            for (Playlist playlist : librarytrue.getPlaylists()) {
                                if (playlist.getOwner().equals(username)) {
                                    ObjectNode playlistObject = objectMappershowPlaylist.createObjectNode();
                                    playlistObject.put("name", playlist.getName());
                                    ArrayNode songsArray = objectMappershowPlaylist.createArrayNode();
                                    ArrayList<Song> piese = playlist.getPiese();
                                    if(piese!=null) {
                                        for (Song song : piese) {
                                            songsArray.add(song.getName());
                                        }
                                    }
                                    playlistObject.set("songs", songsArray);
                                    playlistObject.put("visibility", playlist.getVisibilty());
                                    playlistObject.put("followers", playlist.getFollowers());
                                    playlistsArray.add(playlistObject);
                                }
                            }
                            showPlaylistResult.set("result", playlistsArray);
                            outputs.add(showPlaylistResult);
                            break;
                        }
                    }
                    break;
                case "showPreferredSongs":
                    // for current user we print all his liked songs
                    for(User user:librarytrue.getUsers()) {
                        if (user.getUsername().equals(username)) {
                            ObjectMapper objectMappershowPreferredSongs = new ObjectMapper();
                            // Create ObjectNode
                            ObjectNode showPreferredSongsResult = objectMappershowPreferredSongs.createObjectNode();
                            showPreferredSongsResult.put("command", commandType);
                            showPreferredSongsResult.put("user", username);
                            showPreferredSongsResult.put("timestamp", timestamp);
                            ArrayNode songsArray = objectMappershowPreferredSongs.createArrayNode();
                            ArrayList<Song> likedSongs = user.getLikedSongs();
                            if(likedSongs!=null) {
                                for (Song song : likedSongs) {
                                    songsArray.add(song.getName());
                                }
                            }
                            showPreferredSongsResult.set("result", songsArray);
                            outputs.add(showPreferredSongsResult);
                            break;
                        }
                    }
                    break;
        }

        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);
    }
}
