 package com.example.kozjava.music_clientV2_1;

 import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kozjava.music_clientV2_1.adapter.PlaylistAdapter;
import com.example.kozjava.music_clientV2_1.database.DatabaseHelper;
import com.example.kozjava.music_clientV2_1.models.Playlist;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

 public class PlaylistActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{


     String id;
     RecyclerView recyclerView;
     private PlaylistAdapter playlistAdapter;
     SQLiteDatabase database;
     SwipeRefreshLayout swipeRefreshLayout;
     DatabaseHelper helper;
     private ArrayList<Playlist> playlists;
     TextView options;



     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         id = getIntent().getStringExtra("id");
         options = (TextView)findViewById(R.id.popUp);
         options.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 PopupMenu popupMenu = new PopupMenu(PlaylistActivity.this, options);
                 popupMenu.inflate(R.menu.create_playlist);
                 popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     @Override
                     public boolean onMenuItemClick(MenuItem item) {
                         switch (item.getItemId())
                         {
                             case R.id.create_pl :
                                 addPlaylist();
                                 break;
                             default:
                                 break;
                         }
                         return false;
                     }
                 });
                 popupMenu.show();

             }
         });

         swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.playlistSwipeRefresh);
         swipeRefreshLayout.setOnRefreshListener(this);
         swipeRefreshLayout.setColorSchemeResources(R.color.primary_dark, R.color.aluminum, R.color.blue);

         playlists = new ArrayList<>();
         helper = new DatabaseHelper(PlaylistActivity.this);
         database = helper.getReadableDatabase();
         Cursor cursor = database.rawQuery("SELECT * FROM "+DatabaseHelper.TABLE_PLAYLIST, new String[]{});
         if (cursor.moveToFirst())
         {
             do
             {
                 Playlist pl = new Playlist(
                         cursor.getString(cursor.getColumnIndex("id")),
                         cursor.getString(cursor.getColumnIndex("name")),
                         cursor.getString(cursor.getColumnIndex("userID")),
                         cursor.getString(cursor.getColumnIndex("songs"))
                 );
                 playlists.add(pl);

             }while (cursor.moveToNext());
         }

         recyclerView = (RecyclerView)findViewById(R.id.playlistRecycleView);
         recyclerView.setHasFixedSize(false);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         playlistAdapter = new PlaylistAdapter(PlaylistActivity.this, playlists, id);
         recyclerView.setAdapter(playlistAdapter);
     }


     @Override
     public void onRefresh() {
         playlists.clear();
         Cursor cursor = database.rawQuery("SELECT * FROM "+DatabaseHelper.TABLE_PLAYLIST, new String[]{});
         if(cursor.moveToFirst())
         {
             do
             {
                 Playlist pl = new Playlist(
                         cursor.getString(cursor.getColumnIndex("id")),
                         cursor.getString(cursor.getColumnIndex("name")),
                         cursor.getString(cursor.getColumnIndex("userID")),
                         cursor.getString(cursor.getColumnIndex("songs"))
                 );
                 playlists.add(pl);

             }while (cursor.moveToNext());
         }
         playlistAdapter.notifyDataSetChanged();
         swipeRefreshLayout.setRefreshing(false);

     }


     private void addPlaylist()
     {

         final ArrayList<String> songs = new ArrayList<>();
         final ArrayList<Integer> items = new ArrayList<>();
         final ArrayList<String> result = new ArrayList<>();
         DatabaseHelper helper = new DatabaseHelper(this);
         SQLiteDatabase database = helper.getReadableDatabase();
         Cursor cusror = database.rawQuery("SELECT * FROM Songs", new String[]{});
         if (cusror.moveToFirst())
         {
             do
             {
                 songs.add(cusror.getString(cusror.getColumnIndex(DatabaseHelper.SONGS_SONG)));
             }while (cusror.moveToNext());
         }
         final boolean[] checkedItems = new boolean[songs.size()];

         final EditText playlistName = new EditText(this);
         LinearLayout layout = new LinearLayout(this);
         layout.setOrientation(LinearLayout.VERTICAL);
         playlistName.setHint("Имя");
         layout.addView(playlistName);
         final AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle("Создание плейлиста");
         builder.setView(layout);

         builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
             }
         });
         builder.setPositiveButton("Далее", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 AlertDialog.Builder  mBulder = new AlertDialog.Builder(PlaylistActivity.this);
                 mBulder.setTitle("Выберите файлы");
                 mBulder.setMultiChoiceItems(songs.toArray(new String[songs.size()]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                         if (isChecked) {
                             if (!items.contains(position)) {
                                 items.add(position);
                             }
                             else{
                                 items.remove(position);
                             }
                         }
                     }
                 });
                 mBulder.setCancelable(false);
                 mBulder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                     }
                 });
                 mBulder.setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         for (int i = 0; i< items.size();i++)
                         {
                             result.add(songs.get(items.get(i)));
                         }
                         //String json = new Gson().toJson(result);
                         JSONArray array = new JSONArray(result);
                         new AddPlaylist(PlaylistActivity.this).execute(id, playlistName.getText().toString(),result.toString());
                         result.clear();
                     }
                 });
                 mBulder.show();


             }
         });

         AlertDialog alert = builder.create();
         alert.show();
     }

     private class AddPlaylist extends AsyncTask<String, Void, String>
     {
         Context context;
         public AddPlaylist(Context context)
         {
             this.context = context;
         }

         @Override
         protected String doInBackground(String... params) {
             InputStream input = null;
             OutputStream output = null;
             HttpURLConnection connection = null;
             String result = null;

             try
             {
                 URL url = new URL("http://musicserver.mycloud.by/playlist/add/:"+params[0]);
                 connection = (HttpURLConnection)url.openConnection();
                 connection.setReadTimeout(10000);
                 connection.setConnectTimeout(15000);
                 connection.setRequestMethod("POST");
                 connection.setRequestProperty("Content-Type", "application/json");
                 connection.setDoOutput(true);

                 JSONObject json = new JSONObject();
                 json.put("name", params[1]);
                 json.put("songs", params[2]);

                 Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                 writer.write(json.toString());
                 writer.close();
                 connection.connect();

                 if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                 {
                     return "Server return HTTP " + connection.getResponseCode();
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
             return result;
         }

         @Override
         protected void onPostExecute(String s) {
             super.onPostExecute(s);
             try
             {
                 JSONObject object = new JSONObject(s);
                 if (object.has("error"))
                 {
                     Toast.makeText(context, object.getString("error"), Toast.LENGTH_SHORT).show();
                 }
             }catch (Exception ex)
             {
                 ex.printStackTrace();
             }
         }
     }
 }
