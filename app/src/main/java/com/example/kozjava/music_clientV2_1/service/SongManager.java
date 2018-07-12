package com.example.kozjava.music_clientV2_1.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.kozjava.music_clientV2_1.database.DatabaseHelper;
import com.example.kozjava.music_clientV2_1.models.Songs;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kozjava on 23.05.2017.
 */

public class SongManager extends BroadcastReceiver {


    ArrayList<Songs> songses;
    DatabaseHelper helper;
    Context context;
    Cursor cursor;

    public SongManager(Context context)
    {
        this.context = context;
    }

    public SongManager()
    {

    }




    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context =context;
        String result = null;
        InputStream responseBody = null;
        HttpURLConnection connection = null;
        URL url;
        int responseCode = 0;
        try {

            url = new URL("http://musicserver.mycloud.by/songs");
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.connect();
            responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP code error : " + responseCode);
            }
            responseBody = connection.getInputStream();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(responseBody));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                result = sb.toString();
                Log.i("TAG", "RESPONSE BODY : " + result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally
        {
            if (responseBody != null)
                try
                {
                    responseBody.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (connection != null)
                connection.disconnect();

            parseSongs(result);

        }

    }

    private void parseSongs(String response)
    {
        try
        {
            if (response.length() == 0)
                throw new Exception("null response");
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        Gson gson = new Gson();
        Type listToken = new TypeToken<ArrayList<Songs>>(){}.getType();
        songses = (ArrayList<Songs>)gson.fromJson(response, listToken);

        updateDb();
        Toast.makeText(context, "БД обновлена", Toast.LENGTH_SHORT).show();
    }

    private void updateDb()
    {

        helper = new DatabaseHelper(this.context);
        SQLiteDatabase database = helper.getWritableDatabase();

        database.delete(DatabaseHelper.TABLE_SONGS, null,null);
        for (int i = 0; i< songses.size(); i++)
        {
            Log.i("TAG", songses.get(i).getId() + "  " + songses.get(i).getSong());
             cursor = database.rawQuery("SELECT id FROM Songs WHERE id=?", new String[]{songses.get(i).getId()});
            if (cursor.moveToFirst())
            {
                do
                {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", songses.get(i).getId());
                    contentValues.put("song", songses.get(i).getSong());
                    contentValues.put("album", songses.get(i).getAlbum());
                    contentValues.put("albumYear", songses.get(i).getAlbumYear());
                    contentValues.put("artist", songses.get(i).getArtist());
                    contentValues.put("description", songses.get(i).getDescription());
                    contentValues.put("text", songses.get(i).getText());
                    contentValues.put("version_key", songses.get(i).getVersion_key());
                    database.update(DatabaseHelper.TABLE_SONGS,contentValues, "id=? AND song=?", new String[]{songses.get(i).getId(), songses.get(i).getSong()});
                }while (cursor.moveToNext());
            }
            else
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", songses.get(i).getId());
                contentValues.put("song", songses.get(i).getSong());
                contentValues.put("album", songses.get(i).getAlbum());
                contentValues.put("albumYear", songses.get(i).getAlbumYear());
                contentValues.put("artist", songses.get(i).getArtist());
                contentValues.put("description", songses.get(i).getDescription());
                contentValues.put("text", songses.get(i).getText());
                contentValues.put("version_key", songses.get(i).getVersion_key());
                database.insert(DatabaseHelper.TABLE_SONGS, null, contentValues);
            }

        }
        if (!cursor.isClosed())
            cursor.close();

    }

    public void startManager()
    {
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SongManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000*60, pendingIntent);
    }
}
