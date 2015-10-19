package com.thetubeteam.mytube;

import com.google.api.client.auth.oauth2.Credential;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.io.IOException;
import java.util.List;

/**
 * Creates a new, private playlist in the authorized user's channel and add
 * a video to that new playlist.
 *
 * @author Jeremy Walker
 */
public class PlaylistUpdates {

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

    /**
     * Authorize the user, create a playlist, and add an item to the playlist.
     *
     */
    public static void init(Credential credential) {

        youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                .setApplicationName("MyTube")
                .build();

    }

    public static YouTube.Playlists.List listPlaylists() throws IOException{
        YouTube.Playlists.List list = youtube.playlists().list("snippet");
        list.setMine(true);
        return list;
    }

    public static List<PlaylistItem> listPlaylistItems(String playlistID) throws IOException{
        YouTube.PlaylistItems.List list = youtube.playlistItems().list("snippet");
        list.setPlaylistId(playlistID);
        PlaylistItemListResponse response = list.execute();
        return response.getItems();
    }

    /**
     * Create a playlist and add it to the authorized account.
     */
    public static Playlist insertPlaylist(String playlistTitle) throws IOException {

        PlaylistSnippet playlistSnippet = new PlaylistSnippet();
        playlistSnippet.setTitle(playlistTitle);
        playlistSnippet.setDescription("A private playlist created with the YouTube API v3");
        PlaylistStatus playlistStatus = new PlaylistStatus();
        playlistStatus.setPrivacyStatus("private");

        Playlist youTubePlaylist = new Playlist();
        youTubePlaylist.setSnippet(playlistSnippet);
        youTubePlaylist.setStatus(playlistStatus);

        YouTube.Playlists.Insert playlistInsertCommand =
                youtube.playlists().insert("snippet,status", youTubePlaylist);
        Playlist playlistInserted = playlistInsertCommand.execute();
        return playlistInserted;
    }

    /**
     * Create a playlist item with the specified video ID and add it to the
     * specified playlist.
     *
     * @param playlistId assign to newly created playlistitem
     * @param videoId    YouTube video id to add to playlistitem
     */
    public static PlaylistItem insertPlaylistItem(String playlistId, String videoId) throws IOException {

        // Define a resourceId that identifies the video being added to the
        // playlist.
        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(videoId);

        // Set fields included in the playlistItem resource's "snippet" part.
        PlaylistItemSnippet playlistItemSnippet = new PlaylistItemSnippet();
        playlistItemSnippet.setTitle("First video in the test playlist");
        playlistItemSnippet.setPlaylistId(playlistId);
        playlistItemSnippet.setResourceId(resourceId);

        // Create the playlistItem resource and set its snippet to the
        // object created above.
        PlaylistItem playlistItem = new PlaylistItem();
        playlistItem.setSnippet(playlistItemSnippet);

        // Call the API to add the playlist item to the specified playlist.
        // In the API call, the first argument identifies the resource parts
        // that the API response should contain, and the second argument is
        // the playlist item being inserted.
        YouTube.PlaylistItems.Insert playlistItemsInsertCommand =
                youtube.playlistItems().insert("snippet,contentDetails", playlistItem);
        PlaylistItem returnedPlaylistItem = playlistItemsInsertCommand.execute();

        return returnedPlaylistItem;
    }

    public static void deletePlaylistItem(String playlistItemID) throws IOException{
        youtube.playlistItems().delete(playlistItemID).execute();
    }

    public static List<Video> videoList(String videoIDs) throws IOException{
        YouTube.Videos.List list = youtube.videos().list("snippet,statistics");
        list.setId(videoIDs);
        VideoListResponse response = list.execute();
        return response.getItems();
    }

}