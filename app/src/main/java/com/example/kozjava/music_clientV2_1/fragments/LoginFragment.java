package com.example.kozjava.music_clientV2_1.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kozjava.music_clientV2_1.R;
import com.example.kozjava.music_clientV2_1.database.DatabaseHelper;
import com.example.kozjava.music_clientV2_1.models.Songs;
import com.example.kozjava.music_clientV2_1.models.User;
import com.example.kozjava.music_clientV2_1.requests.SoapAsyncRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.serialization.SoapObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, SoapAsyncRequest.OnPostExecuteListener {

    View view;
    Context context;
    Button login;
    EditText nickname, password;
    TextView relateToSignUp;
    FragmentTransaction fragmentTransaction;
    SignUpFragment signUpFragment;
    DatabaseHelper helper;

    public LoginFragment(Context context) {
        // Required empty public constructor
        this.context = context;
        helper = new DatabaseHelper(this.context);
    }

    public LoginFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_login, container, false);

        view = inflater.inflate(R.layout.fragment_login, null);
        login = (Button)view.findViewById(R.id.btn_login);
        nickname = (EditText)view.findViewById(R.id.input_email);
        password = (EditText)view.findViewById(R.id.input_password);
        relateToSignUp = (TextView)view.findViewById(R.id.link_signup);
        login.setOnClickListener(this);
        relateToSignUp.setOnClickListener(this);

        return view;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_login :
                //Toast.makeText(context, "Hi", Toast.LENGTH_SHORT).show();
                login();
                break;
            case R.id.link_signup :
                signUp();
                break;
        }

    }



    private void signUp()
    {
        signUpFragment = new SignUpFragment(context);
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, signUpFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void login()
    {
        String nick, pass;
        nick = nickname.getText().toString();
        pass = password.getText().toString();

        if (nick.length() == 0 || pass.length() == 0) {
            Toast.makeText(context,"Поля 'Ник' и 'Пароль' обязательны для ввода", Toast.LENGTH_SHORT).show();
        }else {
            new SoapAsyncRequest(LoginFragment.this, context).execute(nick,pass);
        }
    }

    @Override
    public void onSoapPostExecute(SoapObject response) {


        if (response.hasProperty("error"))
        {
            if (response.getPropertyAsString("error").equals("Добро пожаловать"))
            {
                String object = response.getPropertyAsString("object");
                Gson gson = new Gson();
                User user = gson.fromJson(object, User.class);
                SQLiteDatabase database = helper.getWritableDatabase();
                Cursor cursor = database.rawQuery("SELECT id, username FROM Users WHERE id=? AND username=?", new String[]{user.get_id(), user.getUsername()});
                if (!cursor.moveToFirst())
                {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DatabaseHelper.USER_ID, user.get_id());
                    contentValues.put(DatabaseHelper.USER_USERNAME, user.getUsername());
                    contentValues.put(DatabaseHelper.USER_PASSWORD, user.getPassword());
                    contentValues.put(DatabaseHelper.USER_EMAIL, user.getEmail());
                    contentValues.put(DatabaseHelper.USER_FIRSTNAME, user.getFirstName());
                    contentValues.put(DatabaseHelper.USER_LASTNAME, user.getLastName());
                    database.insert(DatabaseHelper.TABLE_USERS, null, contentValues);
                }


                HomeFragment home = new HomeFragment(context);
                Bundle bundle = new Bundle();
                bundle.putString("id", user.get_id());
                bundle.putString("name", user.getUsername());
                home.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainFragment, home);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
            Toast.makeText(context, String.valueOf(response.getPropertyAsString("error")), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Не обработано", Toast.LENGTH_SHORT).show();
        }



    }
}
