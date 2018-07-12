package com.example.kozjava.music_clientV2_1.fragments;


import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kozjava.music_clientV2_1.PlaylistActivity;
import com.example.kozjava.music_clientV2_1.R;
import com.example.kozjava.music_clientV2_1.RecycleActivity;
import com.example.kozjava.music_clientV2_1.database.DatabaseHelper;
import com.example.kozjava.music_clientV2_1.service.PlaylistManager;

import static android.content.Context.ALARM_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    Context context;
    View view;
    TextView nickname, email, name, surname, id, top;
    Button music, playlist;
    DatabaseHelper helper;
    String recieveId = null;
    String recieveName = null;
    int timing = 30;





    public HomeFragment(Context context) {
        this.context = context;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null)
        {
            recieveId = bundle.getString("id");
            recieveName = bundle.getString("name");
        }


        view = inflater.inflate(R.layout.fragment_home, null);

        top = (TextView)view.findViewById(R.id.helloText);
        nickname = (TextView)view.findViewById(R.id.helloid);
        email = (TextView)view.findViewById(R.id.helloemail);
        name = (TextView)view.findViewById(R.id.helloname);
        surname = (TextView)view.findViewById(R.id.hellosurname);
        id = (TextView)view.findViewById(R.id.helloiddown);

        music = (Button)view.findViewById(R.id.btn_music);
        playlist = (Button)view.findViewById(R.id.btn_playlist);

        music.setOnClickListener(this);
        playlist.setOnClickListener(this);

        fillCardView();

        if (isOnline(context))
        {
            AlarmManager manager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(context, PlaylistManager.class);
            intent.putExtra("id", id.getText().toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),60000, pendingIntent);
        }
        return view;
    }


    private void fillCardView()
    {
        helper = new DatabaseHelper(context);
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM Users WHERE id=? AND username=?", new String[]{recieveId, recieveName});
        if (cursor.moveToFirst())
        {
            top.setText("Добро пожаловать " + cursor.getString(3) +"!");
            nickname.setText(cursor.getString(1));
            email.setText(cursor.getString(2));
            name.setText(cursor.getString(3));
            surname.setText(cursor.getString(4));
            id.setText(cursor.getString(0));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_music :

                Intent intent = new Intent(context, RecycleActivity.class);
                startActivity(intent);

                break;
            case R.id.btn_playlist :
                Intent intent1 = new Intent(context, PlaylistActivity.class);
                intent1.putExtra("id", id.getText().toString());
                startActivity(intent1);
                break;
        }

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
