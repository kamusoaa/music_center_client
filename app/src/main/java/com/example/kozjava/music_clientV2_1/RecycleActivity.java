package com.example.kozjava.music_clientV2_1;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kozjava.music_clientV2_1.adapter.DataAdapter;
import com.example.kozjava.music_clientV2_1.database.DatabaseHelper;
import com.example.kozjava.music_clientV2_1.models.Songs;

import java.util.ArrayList;

public class RecycleActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{


    DatabaseHelper helper;
    ArrayList<Songs> list;
    ArrayList<String> songs;
    RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    SQLiteDatabase database;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_recycle);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_dark, R.color.aluminum, R.color.blue);


        list = new ArrayList<>();
        helper = new DatabaseHelper(RecycleActivity.this);
        database = helper.getReadableDatabase();
        songs = new ArrayList<String>();

        if (getIntent().getStringArrayListExtra("songs") != null) {
            songs = getIntent().getStringArrayListExtra("songs");
            for (int i = 0; i < songs.size(); i++) {
                Cursor cursor = database.rawQuery("SELECT * FROM Songs WHERE song=?", new String[]{songs.get(i)});
                if (cursor.moveToFirst()) {
                    do {
                        Songs song = new Songs(
                                cursor.getString(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("artist")),
                                cursor.getString(cursor.getColumnIndex("song")),
                                cursor.getString(cursor.getColumnIndex("description")),
                                cursor.getString(cursor.getColumnIndex("album")),
                                cursor.getString(cursor.getColumnIndex("albumYear")),
                                cursor.getString(cursor.getColumnIndex("text")),
                                cursor.getInt(cursor.getColumnIndex("version_key"))
                        );
                        list.add(song);

                    } while (cursor.moveToNext());
                }
            }
        }
        else
        {
            Cursor cursor = database.rawQuery("SELECT * FROM Songs", new String[]{});
            if (cursor.moveToFirst())
            {
                do
                {
                    Songs song = new Songs(
                            cursor.getString(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("artist")),
                            cursor.getString(cursor.getColumnIndex("song")),
                            cursor.getString(cursor.getColumnIndex("description")),
                            cursor.getString(cursor.getColumnIndex("album")),
                            cursor.getString(cursor.getColumnIndex("albumYear")),
                            cursor.getString(cursor.getColumnIndex("text")),
                            cursor.getInt(cursor.getColumnIndex("version_key"))
                    );
                    list.add(song);

                }while (cursor.moveToNext());
            }
        }


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataAdapter = new DataAdapter(list, RecycleActivity.this);
        recyclerView.setAdapter(dataAdapter);

        //list.clear();



    }

    @Override
    public void onRefresh() {
        list.clear();
        if (songs.size() != 0)
        {
            for (int i = 0; i < songs.size(); i++) {
                Cursor cursor = database.rawQuery("SELECT * FROM Songs WHERE song=?", new String[]{songs.get(i)});
                if (cursor.moveToFirst()) {
                    do {
                        Songs song = new Songs(
                                cursor.getString(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("artist")),
                                cursor.getString(cursor.getColumnIndex("song")),
                                cursor.getString(cursor.getColumnIndex("description")),
                                cursor.getString(cursor.getColumnIndex("album")),
                                cursor.getString(cursor.getColumnIndex("albumYear")),
                                cursor.getString(cursor.getColumnIndex("text")),
                                cursor.getInt(cursor.getColumnIndex("version_key"))
                        );
                        list.add(song);

                    } while (cursor.moveToNext());
                }
            }
        }
        else
        {
            Cursor cursor = database.rawQuery("SELECT * FROM Songs", new String[]{});
            if (cursor.moveToFirst())
            {
                do
                {
                    Songs song = new Songs(
                            cursor.getString(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("artist")),
                            cursor.getString(cursor.getColumnIndex("song")),
                            cursor.getString(cursor.getColumnIndex("description")),
                            cursor.getString(cursor.getColumnIndex("album")),
                            cursor.getString(cursor.getColumnIndex("albumYear")),
                            cursor.getString(cursor.getColumnIndex("text")),
                            cursor.getInt(cursor.getColumnIndex("version_key"))
                    );
                    list.add(song);

                }while (cursor.moveToNext());
            }
        }

        dataAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

    }


}
