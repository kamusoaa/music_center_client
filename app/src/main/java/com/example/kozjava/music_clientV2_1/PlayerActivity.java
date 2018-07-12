package com.example.kozjava.music_clientV2_1;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kozjava.music_clientV2_1.database.DatabaseHelper;

import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    TextView song, artist, album, albumYear, text;
    String name = null;
    private MediaPlayer mediaPlayer;

    private Button b1,b2,b3,b4;
    TextView time, test;
    SeekBar seekBar;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView tx1,tx2,tx3;

    public static int oneTimeOnly = 0;
    boolean isCanceled = false;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isCanceled = true;
        startTime = 0;
        finalTime = 0;
        mediaPlayer.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        name  = getIntent().getStringExtra("name");

        song = (TextView)findViewById(R.id.nameSong);
        artist = (TextView)findViewById(R.id.artistSong);
        album = (TextView)findViewById(R.id.albumSong);
        albumYear = (TextView)findViewById(R.id.albumYearSong);
        text = (TextView)findViewById(R.id.textSong);

        b1 = (Button)findViewById(R.id.next);
        b2 = (Button)findViewById(R.id.pause);
        b3 = (Button)findViewById(R.id.playbt);
        b4 = (Button)findViewById(R.id.prev);

        time = (TextView)findViewById(R.id.currentTime);
        test = (TextView)findViewById(R.id.dontremember);

        seekBar = (SeekBar)findViewById(R.id.seekBar);

        try
        {
            Uri uri = Uri.parse("http://musicserver.mycloud.by/song/:"+name);
            mediaPlayer = MediaPlayer.create(this, uri);
            seekbar = (SeekBar)findViewById(R.id.seekBar);
            seekbar.setClickable(false);
            b2.setEnabled(false);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }




        DatabaseHelper helper = new DatabaseHelper(PlayerActivity.this);
        SQLiteDatabase database = helper.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM Songs WHERE song=?", new String[]{name});
        if (cursor.moveToFirst())
        {
            song.setText(cursor.getString(5));
            artist.setText(cursor.getString(1));
            album.setText(cursor.getString(3));
            albumYear.setText(cursor.getString(4));
            text.setText(cursor.getString(6));
        }




        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }

                test.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        finalTime)))
                );

                time.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        startTime)))
                );

                seekbar.setProgress((int)startTime);
                myHandler.postDelayed(UpdateSongTime,100);
                b2.setEnabled(true);
                b3.setEnabled(false);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                b2.setEnabled(false);
                b3.setEnabled(true);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp+forwardTime)<=finalTime){
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"5 секунд вперед",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"меньше 5 секунд",Toast.LENGTH_SHORT).show();
                }
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int)startTime;

                if((temp-backwardTime)>0){
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(),"5 секунд назад",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"меньше 5 секунд",Toast.LENGTH_SHORT).show();
                }
            }
        });








    }





    private Runnable UpdateSongTime = new Runnable() {
        public void run() {

            if (!isCanceled)
            {
                startTime = mediaPlayer.getCurrentPosition();
                time.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime)))
                );
                seekbar.setProgress((int)startTime);
                myHandler.postDelayed(this, 100);
            }
            else
            {
                seekbar.setProgress((int)startTime);
                Thread.interrupted();
            }
        }
    };
}
