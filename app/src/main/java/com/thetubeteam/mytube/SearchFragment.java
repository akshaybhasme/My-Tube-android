package com.thetubeteam.mytube;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.api.services.youtube.model.SearchResult;
import com.thetubeteam.mytube.models.Video;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {


    private VideoListAdapter adapter;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ListView videoListView;

        videoListView = new ListView(getActivity());
        adapter = new VideoListAdapter(getActivity());
        videoListView.setAdapter(adapter);

        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                watchYoutubeVideo(adapter.getVideo(i).getId());
            }
        });

        return videoListView;
    }

    public void search(String query){
        new SearchTask(query).execute();
    }

    public void watchYoutubeVideo(String id){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+id));
            startActivity(intent);
        }
    }

    public class SearchTask extends AsyncTask<Void, Integer, List<Video>>{

        private String query;

        public SearchTask(String query){
            this.query = query;
        }

        @Override
        protected List<Video> doInBackground(Void... voids) {
            List<Video> videos = new ArrayList<>();

            List<SearchResult> searchResults = SearchUtil.search(query);

            if(searchResults != null){
                for(int i = 0; i < searchResults.size(); i++){
                    SearchResult searchResult = searchResults.get(i);
                    Video video = new Video();
                    video.setId(searchResult.getId().getVideoId());
                    video.setName("" + searchResult.getSnippet().getTitle());
                    video.setDesc("" + searchResult.getSnippet().getPublishedAt());
                    video.setThumbnail(""+searchResult.getSnippet().getThumbnails().getDefault().getUrl());
                    videos.add(video);
                }
            }
            return videos;
        }

        @Override
        protected void onPostExecute(List<Video> searchResults) {
            adapter.setVideos(searchResults);
            super.onPostExecute(searchResults);
        }
    }
}
