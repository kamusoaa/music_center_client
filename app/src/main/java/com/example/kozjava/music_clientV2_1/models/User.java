package com.example.kozjava.music_clientV2_1.models;

/**
 * Created by kozjava on 23.05.2017.
 */

public class User {
    private String _id;
    private String lastName;
    private String firstName;
    private String email;
    private String password;
    private String username;

    public User(String _id, String lastName, String firstName, String email, String password, String username) {
        this._id = _id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public User()
    {

    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
