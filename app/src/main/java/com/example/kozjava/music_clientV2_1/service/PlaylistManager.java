package com.example.kozjava.music_clientV2_1.service;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.kozjava.music_clientV2_1.database.DatabaseHelper;
import com.example.kozjava.music_clientV2_1.models.Playlist;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kozjava on 27.5.17.
 */

public class PlaylistManager extends BroadcastReceiver {

    ArrayList<String> songsList = new ArrayList<>();
    ArrayList<Playlist> playlists = new ArrayList<>();
    Playlist playlist;
    Context context;
    String id;


    @Override
    public void onReceive(Context context, Intent intent) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        String result = null;
        this.context = context;
        id = intent.getStringExtra("id");
        try
        {
            URL url = new URL("http://musicserver.mycloud.by/playlist/:"+id);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");

            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                Log.i("TAG", String.valueOf(connection.getResponseCode()));
                //return connection.getResponseCode();

            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String line = null;
            while((line = reader.readLine())!= null)
            {
                buffer.append(line + "\n");
            }
            result = buffer.toString();
            if (connection != null)
                connection.disconnect();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        parsePlaylist(result);
        updateDB();
        Toast.makeText(context, "Плейлист обновлен", Toast.LENGTH_SHORT).show();
    }


    private void parsePlaylist(String response)
    {

        try
        {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i< array.length();i++)
            {
                JSONObject object = array.getJSONObject(i);
                String id = object.getString("_id");
                String name = object.getString("name");
                String userID = object.getString("userID");
                String songs = object.getString("songs");


                JSONArray innerArray = new JSONArray(songs);
                for (int j = 0; j < innerArray.length();j++)
                {
                    JSONObject innerObjext = innerArray.getJSONObject(j);
                    String songName = innerObjext.getString("songName");
                    songsList.add(songName);
                }
                String json = new Gson().toJson(songsList);
                playlist = new Playlist(id, name, userID, json);
                playlists.add(playlist);
                songsList.clear();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void updateDB()
    {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete(DatabaseHelper.TABLE_PLAYLIST, null, null);
        for (int i = 0; i< playlists.size();i++)
        {
            ContentValues content = new ContentValues();
            content.put(DatabaseHelper.PLAYLIST_ID, playlists.get(i).getId());
            content.put(DatabaseHelper.PLAYLIST_NAME, playlists.get(i).getName());
            content.put(DatabaseHelper.PLAYLIST_USERID, playlists.get(i).getUserID());
            content.put(DatabaseHelper.PLAYLIST_SONGS, playlists.get(i).getJsonArray());
            database.insert(DatabaseHelper.TABLE_PLAYLIST, null, content);
        }
    }

    public void startManager()
    {

    }
}
