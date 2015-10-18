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
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.thetubeteam.mytube.models.Video;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PlaylistFragment extends Fragment {

    public static final String TAG = "PlaylistFragment";

    private SharedPreferences prefs;
    private OAuth2Helper oAuth2Helper;

    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    public PlaylistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        oAuth2Helper = new OAuth2Helper(this.prefs);

        ListView videoListView;
        VideoListAdapter adapter;

        videoListView = new ListView(getActivity());
        adapter = new VideoListAdapter(getActivity());
        videoListView.setAdapter(adapter);

        List<Video> videos = new ArrayList<>();

        for(int i = 0; i < 20; i++){
            Video video = new Video();
            video.setName("Title "+i);
            video.setDesc("Description "+i);
            videos.add(video);
        }

        adapter.setVideos(videos);

        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        performApiCall();

        return videoListView;
    }

    /**
     * Performs an authorized API call.
     */
    private void performApiCall() {
        new ApiCallExecutor().execute();
    }

    private class ApiCallExecutor extends AsyncTask<Uri, Void, Void> {

        String apiResponse = null;

        @Override
        protected Void doInBackground(Uri...params) {

            try {
//                apiResponse = oAuth2Helper.executeApiCall();
//                Log.i(Constants.TAG, "Received response from API : " + apiResponse);
                PlaylistUpdates.init(oAuth2Helper.loadCredential());
                PlaylistListResponse response = PlaylistUpdates.listPlaylists().execute();
                List<Playlist> playlists = response.getItems();
                for(int i = 0; i < playlists.size(); i++){
                    Playlist playlist = playlists.get(i);
                    Log.e(TAG, playlist.getSnippet().getTitle());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                apiResponse=ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            super.
//            txtApiResponse.setText(apiResponse);
        }

    }
}

