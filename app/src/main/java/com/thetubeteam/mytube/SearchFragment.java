package com.thetubeteam.mytube;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
                //watchYoutubeVideo(adapter.getVideo(i).getId());
                Log.d("bbbbb","*************************************************");
            }
        });

        videoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,
                                           int i, long l) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create(); //Read Update
                alertDialog.setTitle("Add");
                alertDialog.setMessage("Add element to Playlist");

                final int index=i;

                alertDialog.setButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addVideoToPlayList(adapter.getVideo(index).getId());
                    }

                });


                alertDialog.show();
                return true;
            }

        });

        return videoListView;
    }


    public  void addVideoToPlayList(String id) {
        //Add video to playlist

    }

    public  void removeVideoFromPlayList(String id) {
        //Remove video from playlist

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
