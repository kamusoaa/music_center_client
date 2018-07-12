package com.example.kozjava.music_clientV2_1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kozjava on 23.05.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper
{

    private Context context;
    private static  int DATABASE_VESRION = 1;
    public static final String DATABASE_NAME = "MusicClient";
    public static final String TABLE_SONGS = "Songs";
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_PLAYLIST = "Playlist";

    public static final String SONGS_ID = "id";
    public static final String SONGS_TEXT = "text";
    public static final String SONGS_ALBUMY = "albumYear";
    public static final String SONGS_ALBUM = "album";
    public static final String SONGS_DESCRIPTION = "description";
    public static final String SONGS_SONG = "song";
    public static final String SONGS_ARTIST = "artist";
    public static final String SONGS_V = "version_key";

    public static final String USER_ID = "id";
    public static final String USER_LASTNAME = "lastName";
    public static final String USER_FIRSTNAME = "firstName";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";
    public static final String USER_USERNAME = "username";

    public static final String PLAYLIST_ID = "id";
    public static final String PLAYLIST_NAME = "name";
    public static final String PLAYLIST_USERID = "userID";
    public static final String PLAYLIST_SONGS = "songs";



    public static String createSongsQuery = "CREATE TABLE " + TABLE_SONGS + " (" +
            SONGS_ID +  " TEXT, " +
            SONGS_ARTIST  + " TEXT, " +
            SONGS_DESCRIPTION +  " TEXT, " +
            SONGS_ALBUM +  " TEXT," +
            SONGS_ALBUMY + " TEXT," +
            SONGS_SONG + " TEXT, " +
            SONGS_TEXT  + " TEXT," +
            SONGS_V + " INTEGER " +
             ")";
    public static String createUserQuery = "CREATE TABLE " + TABLE_USERS + " (" +
            USER_ID + " TEXT, " +
            USER_USERNAME + " TEXT, " +
            USER_EMAIL + " TEXT, " +
            USER_FIRSTNAME + " TEXT, " +
            USER_LASTNAME +" TEXT, " +
            USER_PASSWORD + " TEXT " +
            ")";

    public static String createPlaylistQuery = "CREATE TABLE " + TABLE_PLAYLIST + " (" +
            PLAYLIST_ID  + " TEXT, " +
            PLAYLIST_NAME + " TEXT, " +
            PLAYLIST_USERID + " TEXT, " +
            PLAYLIST_SONGS +  " TEXT" +
            ")";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VESRION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createSongsQuery);
        db.execSQL(createUserQuery);
        db.execSQL(createPlaylistQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
