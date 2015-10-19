package com.thetubeteam.mytube.models;

public class Video {

    private String id;
    private String name;
    private String desc;
    private String thumbnail;
    private String link;
    private boolean isFavorite;
    private String playlistItemID;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setIsFavorite(boolean b) {
        this.isFavorite= b;
    }

    public void setPlaylistItemID(String playlistItemID){
        this.playlistItemID = playlistItemID;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getLink() {
        return link;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public String getPlaylistItemID() {
        return playlistItemID;
    }

}
