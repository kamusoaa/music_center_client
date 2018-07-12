package com.example.kozjava.music_clientV2_1.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kozjava.music_clientV2_1.PlayerActivity;
import com.example.kozjava.music_clientV2_1.R;
import com.example.kozjava.music_clientV2_1.database.DatabaseHelper;
import com.example.kozjava.music_clientV2_1.models.Songs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kozjava on 24.05.2017.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>
{

    private ArrayList<Songs> listItems;
    private Context mContext;
    private ProgressDialog progressDialog;

    public DataAdapter(ArrayList<Songs> listItems, Context context)
    {
        this.mContext = context;
        this.listItems = listItems;
    }


    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DataAdapter.ViewHolder holder, final int position) {

        final Songs itemList = listItems.get(position);
        holder.textTitle.setText(itemList.getSong());
        holder.txtDescription.setText(itemList.getArtist());
        holder.txtOptionDigit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.txtOptionDigit);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId())
                        {
                            case R.id.mnu_item_save :
                                downloadFile(holder.textTitle.getText().toString());
                                break;
                            case R.id.mnu_item_delete :
                                deleteFile(holder.textTitle.getText().toString());
                                break;
                            case R.id.mnu_item_change :
                                Songs song = listItems.get(position);
                                changeFile(song);
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, holder.textTitle.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("name", holder.textTitle.getText().toString());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textTitle, txtDescription, txtOptionDigit;
        public CardView cardView;
        public ViewHolder(View itemView) {
            super(itemView);
            textTitle = (TextView)itemView.findViewById(R.id.txtTitle);
            txtDescription = (TextView)itemView.findViewById(R.id.txtDescription);
            txtOptionDigit = (TextView)itemView.findViewById(R.id.txtOptionDigit);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
        }
    }









    private void downloadFile(String name)
    {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Загрузка началась");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);

        final DownloadFile downloader = new DownloadFile(mContext);
        downloader.execute(name);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloader.cancel(true);
            }
        });

    }

    private void changeFile(final Songs songs)
    {
        final EditText artist = new EditText(mContext);
        final EditText song = new EditText(mContext);
        final EditText description = new EditText(mContext);
        final EditText album = new EditText(mContext);
        final EditText albumYear = new EditText(mContext);
        final EditText text = new EditText(mContext);

        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);

        artist.setText(songs.getArtist());
        artist.setHint("Группа");
        layout.addView(artist);
        song.setText(songs.getSong());
        song.setRawInputType(0x00000000);
        song.setHint("Песня");
        layout.addView(song);
        description.setText(songs.getDescription());
        description.setHint("Описание");
        layout.addView(description);
        album.setText(songs.getAlbum());
        album.setHint("Альбом");
        layout.addView(album);
        albumYear.setText(songs.getAlbumYear());
        albumYear.setHint("Год");
        layout.addView(albumYear);
        text.setText(songs.getText());
        text.setHint("Текст песни");
        layout.addView(text);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Обновление песни");
        builder.setView(layout);


        builder.setPositiveButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new UpdateFile(mContext).execute(
                        songs.getId(),
                        artist.getText().toString(),
                        song.getText().toString(),
                        description.getText().toString(),
                        album.getText().toString(),
                        albumYear.getText().toString(),
                        text.getText().toString()
                );
            }
        });

        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteFile(final String name)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Удаление");
        builder.setMessage("Удалить файл?");
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteFile(mContext,name).execute(name);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();



    }



    private class DownloadFile extends AsyncTask<String, Integer, String>
    {

        Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadFile(Context context)
        {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try
            {
                URL url = new URL("http://musicserver.mycloud.by/song/:"+params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
                    return "Server return HTTP " + connection.getResponseCode();
                }

                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                File soundDirectory = new File("/sdcard/ServerSongs");
                if (!soundDirectory.exists())
                    soundDirectory.mkdirs();
                File outputFile = new File(soundDirectory, params[0]);
                output = new FileOutputStream(outputFile);

                byte[] data = new byte[4096];
                long total = 0;
                int count;

                while ((count = input.read(data)) != -1) {
                    if (isCancelled())
                    {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int)(total * 100 / fileLength));
                    output.write(data, 0 ,count);
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                if (output != null)
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (input != null)
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (connection !=null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm  = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s != null)
                Toast.makeText(context, "Ошибка загрузки "  +s, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Файл загружен", Toast.LENGTH_SHORT).show();
        }
    }

    public class UpdateFile extends AsyncTask<String, Void, String>
    {
        Context context;

        public UpdateFile(Context context)
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
                URL url = new URL("http://musicserver.mycloud.by/updatesong/");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("id", params[0]);
                json.put("artist", params[1]);
                json.put("song", params[2]);
                json.put("description", params[3]);
                json.put("album", params[4]);
                json.put("albumYear", params[5]);
                json.put("text", params[6]);

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
        };

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("TAG", s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.has("error"))
                    Toast.makeText(context, object.getString("error"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DeleteFile extends AsyncTask<String, Void, String>
    {
        Context cotext;
        String name;
        public DeleteFile(Context contex, String name)
        {
            this.cotext = contex;
            this.name = name;
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String result = null;
            try
            {
                URL url = new URL("http://musicserver.mycloud.by/deletesong/");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("song", params[0]);

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
        };

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("TAG", s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.has("error"))
                {
                    if (object.getString("error").equals("Файл удален"))
                    {
                        DatabaseHelper helper = new DatabaseHelper(mContext);
                        SQLiteDatabase database = helper.getWritableDatabase();
                        database.delete("Songs", "song=?", new String[]{name});
                    }
                    Toast.makeText(cotext, object.getString("error"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}