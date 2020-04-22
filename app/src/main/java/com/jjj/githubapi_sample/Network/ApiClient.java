package com.jjj.githubapi_sample.Network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.github.com/";

    // 이미지 URL - https://avatars0.githubusercontent.com/u/4
    private static final String IMAGE_BASE_URL = "https://avatars0.githubusercontent.com/u/";


    public static ApiClient ourInstance = null;
    private static Retrofit retrofit = null;

    public ApiClient() {
        if (ourInstance == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())
                    .build();
        }
    }

    public static Retrofit getInstance() {
        if (ourInstance == null) {
            ourInstance = new ApiClient();
        }

        return retrofit;
    }

    public HttpLoggingInterceptor getIntercepter() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        return interceptor;
    }

    public OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(getIntercepter())
                .build();

        return client;
    }
}