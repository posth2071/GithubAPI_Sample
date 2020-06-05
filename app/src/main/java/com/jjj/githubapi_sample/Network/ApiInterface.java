package com.jjj.githubapi_sample.Network;

import com.jjj.githubapi_sample.Model.User;
import com.jjj.githubapi_sample.Model.UserList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface ApiInterface {

    // GET 요청, User 목록 요청 (MainCall)
    @GET("users")
    Call<List<UserList>> getUsers(@Header("Authorization") String token);


    // GET 요청, 각 User 세부 정보 요청 (SubCall)
    @GET("users/{login}")	// {login} 부분을 함수인자로 전달받도록 설정
    Call<User> getUserInfo(
            @Header("Authorization") String token,
            @Path("login") String login
    );
}