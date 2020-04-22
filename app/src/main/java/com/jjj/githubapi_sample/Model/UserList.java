package com.jjj.githubapi_sample.Model;

import com.google.gson.annotations.SerializedName;

public class UserList {
    @SerializedName("login")
    private String login;
    @SerializedName("id")
    private int id;


    public UserList(String login, int id) {
        this.login = login;
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}