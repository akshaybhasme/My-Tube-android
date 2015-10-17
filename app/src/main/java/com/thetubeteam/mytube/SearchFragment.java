package com.thetubeteam.mytube;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.thetubeteam.mytube.models.Video;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        return videoListView;
    }
}
