package com.jjj.githubapi_sample.Model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("login")		// 'login' - 이름
    private String login;

    @SerializedName("id")		// 'id' - 순서
    private int id;

    @SerializedName("avatar_url")	// 'avatar_url' - 프로필사진 URL
    private String image;

    @SerializedName("blog")		// 'blog' - 블로그 URL
    private String blog;

    @SerializedName("location")		// 'location' - 지역
    private String location;

    @SerializedName("followers")	// 'followers' - 팔로워 수
    private int followers;

    @SerializedName("following")	// 'following' - 팔로윙 수
    private int following;

    // User 생성자(Constructor)
    public User(String login, int id, String image, String blog, String location, int followers, int following) {
        this.login = login;
        this.id = id;
        this.image = image;
        this.blog = blog;
        this.location = location;
        this.followers = followers;
        this.following = following;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }
}