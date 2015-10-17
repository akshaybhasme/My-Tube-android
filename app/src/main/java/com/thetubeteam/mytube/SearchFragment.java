package com.thetubeteam.mytube;

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

    private ListView videoListView;
    private VideoListAdapter adapter;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        videoListView = new ListView(getActivity());
        adapter = new VideoListAdapter(getActivity());
        videoListView.setAdapter(adapter);

        new SearchTask().execute();

        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        return videoListView;
    }

    public class SearchTask extends AsyncTask<Void, Integer, List<Video>>{

        @Override
        protected List<Video> doInBackground(Void... voids) {
            List<Video> videos = new ArrayList<>();

            List<SearchResult> searchResults = SearchUtil.search("android");

            if(searchResults != null){
                for(int i = 0; i < searchResults.size(); i++){
                    SearchResult searchResult = searchResults.get(i);
                    Video video = new Video();
                    video.setId(searchResult.getId().getVideoId());
                    video.setName("Title "+searchResult.getSnippet().getTitle());
                    video.setDesc("Description "+searchResult.getSnippet().getDescription());
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
