package com.thetubeteam.mytube;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.thetubeteam.mytube.models.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends BaseAdapter {

    private List<Video> videos = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    public VideoListAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setVideos(List<Video> videos){
        this.videos = videos;
        notifyDataSetChanged();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_video, new LinearLayout(context));
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.tvVideoTitle);
            viewHolder.desc = (TextView) convertView.findViewById(R.id.tvVideoDesc);
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvVideoTitle, viewHolder.name);
            convertView.setTag(R.id.tvVideoDesc, viewHolder.desc);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(videos.get(position).getName());
        viewHolder.desc.setText(videos.get(position).getDesc());

        return convertView;
    }

    public static class ViewHolder{
        ImageView thumbail;
        TextView name, desc;
    }
}
