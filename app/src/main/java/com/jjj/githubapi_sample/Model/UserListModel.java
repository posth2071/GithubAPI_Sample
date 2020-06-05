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

        private final String GITHUB_TOKEN = "token a0fb5fd98397e898dc0df95c27c818b94c5c3020";   // Github API Token - 시간당 Request 제한으로 필요

        List<User> users = new ArrayList<>();
        int count = 0;      // SubCall 마지막 통신결과 구분위한 count

    // UserListContract.Model 인터페이스 메서드 구현
    @Override
    public void getUserList(final onFinishedListener onFinishedListener) {

        // Retrofit 인스턴스를 통해 Retrofit 인터페이스 구현
        final ApiInterface service = ApiClient.getInstance()
                .create(ApiInterface.class);

        Call<List<UserList>> call = service.getUsers(GITHUB_TOKEN);

        // MainCall 비동기요청(enqueue) - Callback 리스너 필요
        call.enqueue(new Callback<List<UserList>>() {
            // onResponse() 구현 - 통신 성공 시 Callback
            @Override
            public void onResponse(Call<List<UserList>> call, Response<List<UserList>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<UserList> userList = response.body();   // 통신 성공 시 결과 추출 - 30명의 User 저장
                count = userList.size();

                for (UserList user : userList) {
                    // 각 User 상세정보 Request 하는 SubCall
                    Call<User> subCall = service.getUserInfo(GITHUB_TOKEN, user.getLogin());

                    subCall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            count--;                            // 통신 성공 시 count 줄이기
                            if (!response.isSuccessful()) {     // 응답Code 체크 - 3xx & 4xx의 실패 코드인지 ?
                                return;
                            }
                            users.add(response.body());
                            // 현재 count가 0일 경우 -> 30번의 User 정보 요청 중 마지막이 완료된 경우
                            if ((count) == 0) {
                                onFinishedListener.onFinished(users);   // onFinishedListener를 통해 Presenter에게 데이터(users) 전달
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