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

        private final String GITHUB_TOKEN = "token {Your Token}";   // Github API Token - 시간당 Request 제한으로 필요

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
                    // Presenter 통신실패 함수 호출 + Log 남기기
                    onFinishedListener.onFailure(
                            RequestFail_Log("MainCall", "onResponse", response)
                    );
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
                            Log.d(TAG, "onResponse: SubCall");
                            count--;                            // 통신 성공 시 count 줄이기

                            if (!response.isSuccessful()) {     // 응답Code 체크 - 3xx & 4xx의 실패 코드인지 ?
                                // Presenter 통신실패 함수 호출 + Log 남기기
                                onFinishedListener.onFailure(
                                        RequestFail_Log("SubCall", "onResponse", response)
                                );
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
                            // Presenter 통신실패 함수 호출 + Log 남기기
                            onFinishedListener.onFailure(
                                    RequestFail_Log("SubCall", "onFailure", t)
                            );
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<UserList>> call, Throwable t) {
                // Presenter 통신실패 함수 호출 + Log 남기기
                onFinishedListener.onFailure(
                        RequestFail_Log("MainCall", "onFailure", t)
                );
            }
        });

    }

    // REST Request 실패 시 Log 표시 함수
    //  : onFailure 또는 onResponse 분기 구분 필요 (onResponse 응답Code가 3xx & 4xx일 경우)
    private String RequestFail_Log(String call, String point, Object result) {
        StringBuilder errorMsg = new StringBuilder();

        if (result instanceof Response) {
            // onResponse에서 응답코드가 3xx & 4xx 일 경우
            Response response = (Response)result;   // Response 타입 캐스팅
            errorMsg.append(String.format("%s: %s Failure, Code [%d] message [%s]", point, call, response.code(), response.message()));
        }
        else if (result instanceof Throwable) {
            // onFailure에서 호출한 경우 (시스템적 예외)
            Throwable t = (Throwable)result;    // Throwable 타입 캐스팅
            errorMsg.append(String.format("%s: %s Failure, message [$s]", point, call, t.getMessage()));
        }
        Log.d(TAG, errorMsg.toString());    // Log 찍기
        return errorMsg.toString();         // 분기구분된 ErrorMsg 반환
    }
}