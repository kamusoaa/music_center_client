package com.example.kozjava.music_clientV2_1.models;

/**
 * Created by kozjava on 23.05.2017.
 */

public class Songs {


    private String _id;
    private String artist;
    private String song;
    private String description;
    private String album;
    private String albumYear;
    private String text;
    private int version_key;


    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumYear() {
        return albumYear;
    }

    public void setAlbumYear(String albumYear) {
        this.albumYear = albumYear;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getVersion_key() {
        return version_key;
    }

    public void setVersion_key(int version_key) {
        this.version_key = version_key;
    }








    public Songs(String id, String artist, String song, String description, String album, String albumYear, String text, int version_key) {

        this._id = id;
        this.artist = artist;
        this.song = song;
        this.description = description;
        this.album = album;
        this.albumYear = albumYear;
        this.text = text;
        this.version_key = version_key;
    }

    public Songs()
    {

    }
}
