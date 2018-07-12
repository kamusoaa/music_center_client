package com.example.kozjava.music_clientV2_1.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kozjava.music_clientV2_1.R;
import com.example.kozjava.music_clientV2_1.RecycleActivity;
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

/**
 * Created by kozjava on 28.5.17.
 */

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private Context context;
    ArrayList<Playlist> list;
    String id;
    int amount = 0;
    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists, String id)
    {
        this.context = context;
        this.list = playlists;
        this.id = id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try
        {
            final Playlist playlist = list.get(position);
            JSONArray array = new JSONArray(playlist.getJsonArray());
            final ArrayList<String> songs = new ArrayList<>();

            for (int i = 0; i< array.length();i++)
            {
                songs.add(array.getString(i));
                amount++;
            }

            holder.textTitle.setText(playlist.getName());
            holder.txtDescription.setText("Количество песен: " + amount);
            amount = 0;
            holder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(context,holder.txtOptionDigit);
                    menu.inflate(R.menu.recycle_playlist_menu);
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())
                            {
                                case R.id.rec_pl_change :
                                    changePlaylist(holder.textTitle.getText().toString());
                                    break;
                                case R.id.rec_pl_delete :
                                    deletePlaylist(holder.textTitle.getText().toString());
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    menu.show();
                }
            });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RecycleActivity.class);
                    intent.putStringArrayListExtra("songs", (ArrayList<String>) songs);
                    context.startActivity(intent);
                }
            });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    private void deletePlaylist(final String plName)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Удаление");
        builder.setMessage("Удалить плейлист?");
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeletePlaylist(context).execute(id,plName);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changePlaylist(final String plName)
    {
        final ArrayList<String> songs = new ArrayList<>();
        final ArrayList<Integer> items = new ArrayList<>();
        final ArrayList<String> result = new ArrayList<>();
        DatabaseHelper helper = new DatabaseHelper(context);
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Изменить плейлист");
        builder.setMultiChoiceItems(songs.toArray(new String[songs.size()]), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    if (!items.contains(which)) {
                        items.add(which);
                    }
                    else{
                        items.remove(which);
                    }
                }
            }
        });

        builder.setCancelable(false);
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i< items.size();i++)
                {
                    result.add(songs.get(items.get(i)));
                }
                JSONArray array = new JSONArray(result);
                new ChangePlaylist(context).execute(id, plName, result.toString());
                result.clear();

            }
        });

        builder.show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textTitle, txtDescription, txtOptionDigit;
        public CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            textTitle = (TextView)itemView.findViewById(R.id.txtTitle);
            txtDescription = (TextView)itemView.findViewById(R.id.txtDescription);
            txtOptionDigit =(TextView)itemView.findViewById(R.id.txtOptionDigit);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
        }
    }

    private class DeletePlaylist extends AsyncTask<String, Void, String>
    {

        Context context;

        public DeletePlaylist(Context context)
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
                URL url = new URL("http://musicserver.mycloud.by/playlist/delete/:"+params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject object = new JSONObject();
                object.put("name", params[1]);
                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(object.toString());
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

    private class ChangePlaylist extends AsyncTask<String, Void, String>
    {
        Context context;
        public ChangePlaylist(Context context)
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
                URL url = new URL("http://musicserver.mycloud.by/playlist/change/:"+params[0]);
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
