package com.example.kozjava.music_clientV2_1.models;

import java.util.ArrayList;

/**
 * Created by kozjava on 27.5.17.
 */

public class Playlist {

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }

    public ArrayList<String> getSongs() {
        return songs;
    }

    private String id;
    private String name;
    private String userID;
    private ArrayList<String> songs;
    private String jsonArray;


    public Playlist(String id, String name, String userID, ArrayList<String> songs) {
        this.id = id;
        this.name = name;
        this.userID = userID;
        this.songs = songs;
    }

    public Playlist(String id, String name, String userID, String jsonArray)
    {
        this.id = id;
        this.name = name;
        this.userID = userID;
        this.jsonArray = jsonArray;

    }

    public String getJsonArray() {
        return jsonArray;
    }
}

