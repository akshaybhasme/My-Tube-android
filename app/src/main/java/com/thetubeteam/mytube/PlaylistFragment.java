package com.thetubeteam.mytube;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.thetubeteam.mytube.models.Video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PlaylistFragment extends Fragment {

    public static final String TAG = "PlaylistFragment";

    private ListView videoListView;
    private VideoListAdapter adapter;

    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    public PlaylistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new VideoListAdapter(getActivity());
        adapter.setPlaylistFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        videoListView = new ListView(getActivity());

        videoListView.setAdapter(adapter);

        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        checkAndAddSJSUPlaylist();

        return videoListView;
    }

    public void refreshList() {
        Log.e(TAG, "refreshing !!!");
        new ListSJSUPlaylistTask().execute();
    }

    /**
     * Performs an authorized API call.
     */
    private void checkAndAddSJSUPlaylist() {
        new FindSJSUPlaylist().execute();
    }

    private class FindSJSUPlaylist extends AsyncTask<Uri, Void, Void> {

        String apiResponse = null;

        @Override
        protected Void doInBackground(Uri...params) {

            try {
                PlaylistListResponse response = PlaylistUpdates.listPlaylists().execute();
                List<Playlist> playlists = response.getItems();
                boolean foundPlaylist = false;
                for(int i = 0; i < playlists.size(); i++){
                    Playlist playlist = playlists.get(i);
                    Log.e(TAG, playlist.getSnippet().getTitle());
                    if(playlist.getSnippet().getTitle().equals(Constants.SJSU_PLAYLIST_NAME)){
                        Constants.SJSU_PLAYLIST = playlist;
                        foundPlaylist = true;
                    }
                }

                if(!foundPlaylist){
                    //Insert Playlist
                    Constants.SJSU_PLAYLIST = PlaylistUpdates.insertPlaylist(Constants.SJSU_PLAYLIST_NAME);
                }

                Log.e(TAG, "SJSU Playlist ID "+Constants.SJSU_PLAYLIST.getId());

            } catch (Exception ex) {
                ex.printStackTrace();
                apiResponse=ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "onPostExecute");
            new ListSJSUPlaylistTask().execute();
        }

    }
    static Set<String> videosList=null;
    private class ListSJSUPlaylistTask extends AsyncTask<Void, Integer, List<Video>>{

        @Override
        protected List<Video> doInBackground(Void... voids) {
            List<Video> videos = new ArrayList<>();

            try{
                List<PlaylistItem> playlistResults = PlaylistUpdates.listPlaylistItems(Constants.SJSU_PLAYLIST.getId());

                StringBuilder videoIDs = new StringBuilder();
                videosList=new HashSet<>();

                for(int i = 0; i < playlistResults.size(); i++){
                    PlaylistItem item = playlistResults.get(i);
                    videoIDs.append(","+item.getSnippet().getResourceId().getVideoId());
                    videosList.add(item.getSnippet().getResourceId().getVideoId());
                }

                List<com.google.api.services.youtube.model.Video> videoList = PlaylistUpdates.videoList(videoIDs.toString());

                for(int i = 0; i < playlistResults.size(); i++){
                    PlaylistItem searchResult = playlistResults.get(i);
                    Video video = new Video();
                    video.setId(searchResult.getSnippet().getResourceId().getVideoId());
                    video.setName("" + searchResult.getSnippet().getTitle());
                    video.setDesc("Published on: " + SearchFragment.formatDate(searchResult.getSnippet().getPublishedAt()) + "\nNumber of Views: " + videoList.get(i).getStatistics().getViewCount());
                    video.setThumbnail("" + searchResult.getSnippet().getThumbnails().getDefault().getUrl());
                    video.setIsFavorite(true);
                    video.setPlaylistItemID(searchResult.getId());
                    videos.add(video);
                }


            }catch (IOException e){
                e.printStackTrace();
            }

            return videos;
        }

        @Override
        protected void onPostExecute(List<Video> videos) {
            adapter.setVideos(videos);
        }
    }
}

