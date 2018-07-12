package com.example.kozjava.music_clientV2_1.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.kozjava.music_clientV2_1.R;

import static android.content.ContentValues.TAG;

/**
 * Created by kozjava on 30.5.17.
 */

public class SongService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();

        Notification.Builder builder = new Notification.Builder(this).setSmallIcon(R.drawable.logo).setContentTitle("Работает фоновая служба");
        Notification notofocation;
        notofocation = builder.build();

        Log.i(TAG, "onCreate: " + this.getApplicationContext());
        SongManager manager = new SongManager(this.getApplicationContext());
        manager.startManager();
        startForeground(666, notofocation);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
