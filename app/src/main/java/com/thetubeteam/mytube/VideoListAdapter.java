package com.thetubeteam.mytube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.thetubeteam.mytube.modals.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends BaseAdapter {

    private List<Video> videos = new ArrayList<>();
    private LayoutInflater inflater;

    public VideoListAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public void setVideos(List<Video> videos){
        this.videos = videos;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return videos.get(i);
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
