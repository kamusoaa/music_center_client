package com.example.kozjava.music_clientV2_1.fragments;


import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
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
import com.example.kozjava.music_clientV2_1.models.User;
import com.example.kozjava.music_clientV2_1.requests.SoapAsyncRequest;
import com.google.gson.Gson;

import org.ksoap2.serialization.SoapObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener, SoapAsyncRequest.OnPostExecuteListener {


    Context context;
    EditText username, password, email, fisrtName, lastName;
    Button signup;
    TextView relateToLogin;
    View view;
    DatabaseHelper helper;
    boolean STATUS = false;

    public SignUpFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }
    public SignUpFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.fragment_sign_up,null);
        username = (EditText)view.findViewById(R.id.input_s_nickname);
        password = (EditText)view.findViewById(R.id.input_s_password);
        email = (EditText)view.findViewById(R.id.input_s_email);
        fisrtName = (EditText)view.findViewById(R.id.input_s_name);
        lastName = (EditText)view.findViewById(R.id.input_s_surname);
        signup = (Button)view.findViewById(R.id.btn_signup);
        relateToLogin = (TextView)view.findViewById(R.id.link_login);
        signup.setOnClickListener(this);
        relateToLogin.setOnClickListener(this);
        helper = new DatabaseHelper(context);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_signup :
                signUp();
                break;
            case R.id.link_login :
                login();
                break;
        }

    }



    private void login()
    {
        LoginFragment loginFragment = new LoginFragment(context);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFragment, loginFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void signUp()
    {
        String name, surname, login, pass, mail;
        name = fisrtName.getText().toString();
        surname = lastName.getText().toString();
        login = username.getText().toString();
        pass = password.getText().toString();
        mail = email.getText().toString();
        if (name.length()==0 || surname.length() ==0 || login.length() == 0 || pass.length() ==0 || mail.length() ==0)
        {
            Toast.makeText(context, "Все поля должны быть проинициализированы", Toast.LENGTH_SHORT).show();
        }
        else
        {
            new SoapAsyncRequest(SignUpFragment.this, context).execute(name, surname, login, pass, mail);
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
            Toast.makeText(context, response.getPropertyAsString("error"), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Нет ответа", Toast.LENGTH_SHORT).show();
        }
    }
}
