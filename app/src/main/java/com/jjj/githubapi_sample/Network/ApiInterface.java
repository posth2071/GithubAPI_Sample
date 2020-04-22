package com.jjj.githubapi_sample.Network;

import com.jjj.githubapi_sample.Model.User;
import com.jjj.githubapi_sample.Model.UserList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ApiInterface {
    @GET("users")
    Call<List<UserList>> getUsers(@Header("Authorization") String token);

    @GET("users/{login}")
    Call<User> getUserInfo(
            @Header("Authorization") String token,
            @Path("login") String login
    );

}