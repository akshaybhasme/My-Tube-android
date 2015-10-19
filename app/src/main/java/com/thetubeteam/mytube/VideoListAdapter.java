package com.thetubeteam.mytube;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.youtube.model.PlaylistItem;
import com.squareup.picasso.Picasso;
import com.thetubeteam.mytube.models.Video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends BaseAdapter {

    public static final String TAG = "VideoListAdapter";

    private List<Video> videos = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;

    private PlaylistFragment playlistFragment;

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

    public Video getVideo(int i) {
        return videos.get(i);
    }

    @Override
    public int getCount() {
        return videos.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_video, new LinearLayout(context));
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.tvVideoTitle);
            viewHolder.desc = (TextView) convertView.findViewById(R.id.tvVideoDesc);
            viewHolder.thumbail = (ImageView) convertView.findViewById(R.id.ivVideoThumbnail);
            viewHolder.rlayout = (RelativeLayout) convertView.findViewById(R.id.RlayoutId);
            viewHolder.favorite = (ImageButton) convertView.findViewById(R.id.favorite);
            final int index=position;
            viewHolder.rlayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    watchYoutubeVideo(videos.get(index).getId());
                }

            });

            viewHolder.favorite.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(videos.get(position).getIsFavorite()){
                       // Remove from playlist
                        String playlistItemID = videos.get(position).getPlaylistItemID();
                        if(playlistItemID != null)
                            new RemoveFromFavoriteTask(playlistItemID).execute();
                        else
                            Toast.makeText(context, "Operation not supported here", Toast.LENGTH_SHORT).show();
                    } else {
                        // Add to Playlist
                        new AddToFavoriteTask(videos.get(position).getId()).execute();
                    }

                }

            });

            if(videos.get(index).getIsFavorite()) {
                String uri = "@drawable/button_pressed";  // where myresource.png is the file
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);
                viewHolder.favorite.setImageDrawable(res);
            }else{
                String uri = "@drawable/button_normal";  // where myresource.png is the file
                int imageResource = context.getResources().getIdentifier(uri, null, context.getPackageName());
                Drawable res = context.getResources().getDrawable(imageResource);
                viewHolder.favorite.setImageDrawable(res);
            }

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvVideoTitle, viewHolder.name);
            convertView.setTag(R.id.tvVideoDesc, viewHolder.desc);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(videos.get(position).getName());
        viewHolder.desc.setText(videos.get(position).getDesc());
        Picasso.with(context).load(videos.get(position).getThumbnail()).into(viewHolder.thumbail);

        return convertView;
    }

    public void watchYoutubeVideo(String id){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            context.startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+id));
            context.startActivity(intent);
        }
    }

    public static class ViewHolder{
        ImageView thumbail;
        TextView name, desc;
        RelativeLayout rlayout;
        ImageButton favorite;
    }

    private class AddToFavoriteTask extends AsyncTask<Void, Integer, PlaylistItem>{

        private String videoID;

        public AddToFavoriteTask(String videoID){
            this.videoID = videoID;
        }

        @Override
        protected PlaylistItem doInBackground(Void... voids) {
            try{
                Log.d(TAG, "Adding to playlist "+videoID);
                 return PlaylistUpdates.insertPlaylistItem(Constants.SJSU_PLAYLIST.getId(), videoID);
            }catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(PlaylistItem playlistItem) {
            if(playlistItem != null){
                Toast.makeText(context, "Video added to playlist", Toast.LENGTH_SHORT).show();
                if(playlistFragment != null){
                    playlistFragment.refreshList();
                }
            }else{
                Toast.makeText(context, "Problem adding to playlist", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class RemoveFromFavoriteTask extends AsyncTask<Void, Integer, Boolean>{

        private String playlistItemID;

        public RemoveFromFavoriteTask(String playlistItemID){
            this.playlistItemID = playlistItemID;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                PlaylistUpdates.deletePlaylistItem(playlistItemID);
                return true;
            }catch (IOException e){
                e.printStackTrace();

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean)
                Toast.makeText(context, "Removed from playlist", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Unable to remove from playlist", Toast.LENGTH_SHORT).show();
            playlistFragment.refreshList();

        }
    }

    public void setPlaylistFragment(PlaylistFragment fragment){
        Log.e(TAG, "Playlist fragment set!!!");
        this.playlistFragment = fragment;
    }
}
