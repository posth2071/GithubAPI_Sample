package com.jjj.githubapi_sample.Model;

import com.google.gson.annotations.SerializedName;

public class UserList {

    @SerializedName("login")    // REST Request 결과 중 저장할 속성 - "login"
    private String login;

    @SerializedName("id")       // REST Request 결과 중 저장할 속성 - "id"
    private int id;

    // UserList 생성자(Constructor)
    public UserList(String login, int id) {
        this.login = login;
        this.id = id;
    }

    // Getter & Setter 메서드
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