package com.thetubeteam.mytube;

import android.content.SharedPreferences;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.json.JsonHttpContent;

import java.io.IOException;

public class PlaylistAPIUtil {

    private OAuth2Helper helper;

    public PlaylistAPIUtil(SharedPreferences prefs){
        helper = new OAuth2Helper(prefs, Oauth2Params.YOUTUBE);
    }

    public String listPlaylists(){
        String url = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&mine=true";
        return doGet(url);
    }

    public String listPlaylistItems(String playlistID){
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId="+playlistID;
        return doGet(url);
    }

    public String addPlaylist(String playlistTitle){
        String url = "https://www.googleapis.com/youtube/v3/playlists?part=snippet";
        return doPost(url, new Object()); // TODO {"snippet": {"title": "playlistTitle"}}
    }

    private String doGet(String url){
        String json = null;
        try{
            json = OAuth2Helper.HTTP_TRANSPORT.createRequestFactory(
                    helper.loadCredential()
            ).buildGetRequest(new GenericUrl(url)).execute().parseAsString();
        }catch (IOException e){
            e.printStackTrace();
        }
        return json;
    }

    private String doPost(String url, Object data){
        String json = null;
        HttpContent content = new JsonHttpContent(OAuth2Helper.JSON_FACTORY, data);
        try{
            json = OAuth2Helper.HTTP_TRANSPORT.createRequestFactory(
                    helper.loadCredential()
            ).buildPostRequest(new GenericUrl(url), content).execute().parseAsString();
        }catch (IOException e){
            e.printStackTrace();
        }
        return json;
    }

}
