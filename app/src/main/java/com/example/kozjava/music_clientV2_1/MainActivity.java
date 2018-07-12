package com.example.kozjava.music_clientV2_1;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.kozjava.music_clientV2_1.fragments.LoginFragment;
import com.example.kozjava.music_clientV2_1.service.SongService;


public class MainActivity extends AppCompatActivity{

    LoginFragment loginFragment;
    FragmentTransaction fragmentTransaction;
    private int timing = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        if (isOnline(MainActivity.this))
        {
            /*
            AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(MainActivity.this, SongManager.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),timing * 2000, pendingIntent);
            */

            startService(new Intent(MainActivity.this, SongService.class));

        }
        else
        {
            Toast.makeText(this, "Нет подключения к Интернету", Toast.LENGTH_SHORT).show();
        }


        loginFragment = new LoginFragment(MainActivity.this);
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mainFragment, loginFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();




    }


    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
}
