package com.jjj.githubapi_sample.Model;

import android.util.Log;

import com.jjj.githubapi_sample.Contract.UserListContract;
import com.jjj.githubapi_sample.Network.ApiClient;
import com.jjj.githubapi_sample.Network.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListModel implements UserListContract.Model {
    private final String TAG = "UserListModel";
    private final String GITHUB_TOKEN = "token ...";

    List<User> users = new ArrayList<>();
    int count = 0;

    @Override
    public void getUserList(final onFinishedListener onFinishedListener) {
        final ApiInterface service = ApiClient.getInstance()
                .create(ApiInterface.class);

        Call<List<UserList>> call = service.getUsers(GITHUB_TOKEN);
        call.enqueue(new Callback<List<UserList>>() {
            @Override
            public void onResponse(Call<List<UserList>> call, Response<List<UserList>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<UserList> userList = response.body();
                count = userList.size();

                for (UserList user : userList) {
                    Call<User> subCall = service.getUserInfo(GITHUB_TOKEN, user.getLogin());

                    subCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            count--;
                            if (!response.isSuccessful()) {
                                return;
                            }
                            users.add(response.body());
                            if ((count) == 0) {
                                onFinishedListener.onFinished(users);
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.d(TAG, t.toString());
                            onFinishedListener.onFailure(t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<UserList>> call, Throwable t) {
                Log.d(TAG, t.toString());
                onFinishedListener.onFailure(t);
            }
        });

    }
}