package com.thetubeteam.mytube;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.SearchResult;
import com.thetubeteam.mytube.models.Video;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchFragment extends Fragment {


    private VideoListAdapter adapter;
    private PlaylistFragment playlistFragment;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new VideoListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ListView videoListView;

        videoListView = new ListView(getActivity());
        videoListView.setAdapter(adapter);
        adapter.setPlaylistFragment(playlistFragment);

        return videoListView;
    }


    public void search(String query){
        new SearchTask(query).execute();
    }

    public void setPlaylistFragment(PlaylistFragment playlistFragment){
        this.playlistFragment = playlistFragment;
    }

    public class SearchTask extends AsyncTask<Void, Integer, List<Video>>{

        private String query;

        public SearchTask(String query){
            this.query = query;
        }

        @Override
        protected List<Video> doInBackground(Void... voids) {

            List<Video> videos = new ArrayList<>();

            try{
                List<SearchResult> searchResults = SearchUtil.search(query);

                StringBuilder videoIDs = new StringBuilder();

                for(int i = 0; i < searchResults.size(); i++){
                    SearchResult searchResult = searchResults.get(i);
                    videoIDs.append(","+searchResult.getId().getVideoId());
                }

                List<com.google.api.services.youtube.model.Video> videoList = PlaylistUpdates.videoList(videoIDs.toString());

                if(searchResults != null){
                    for(int i = 0; i < searchResults.size(); i++){
                        SearchResult searchResult = searchResults.get(i);
                        Video video = new Video();
                        video.setId(searchResult.getId().getVideoId());
                        video.setName("" + searchResult.getSnippet().getTitle());
                        video.setDesc("Published on: " + formatDate(searchResult.getSnippet().getPublishedAt())+"\nNumber of Views: "+videoList.get(i).getStatistics().getViewCount());
                        video.setThumbnail("" + searchResult.getSnippet().getThumbnails().getDefault().getUrl());
                        videos.add(video);

                    }
                }


            }catch (IOException e){
                e.printStackTrace();
            }

            return videos;
        }

        @Override
        protected void onPostExecute(List<Video> searchResults) {
            adapter.setVideos(searchResults);
            super.onPostExecute(searchResults);
        }
    }

    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDate(DateTime dateTime){
        try{
            Date date = format.parse(dateTime.toString());
            return format.format(date);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

}
