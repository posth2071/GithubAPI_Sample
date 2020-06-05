package com.jjj.githubapi_sample.Contract;

import com.jjj.githubapi_sample.Model.User;

import java.util.List;

public interface UserListContract {
    // View - Presenter 연결 인터페이스 - View 구현 & Presneter 호출
    interface View {
        void showProgress();

        void hideProgress();

        void showToast(String message);

        void onResponseFailure(Throwable throwable);    // 통신 실패 시 Presenter가 View 메서드 호출
    }

    // Presenter - Model 연결 인터페이스
    interface Model {
        // Presenter 구현 & View 호출
        interface onFinishedListener {
            void onFinished(List<User> users);  // Request 성공 시

            void onFailure(Throwable t);        // Request 실패 시
        }

        // Model 구현 & Presenter 호출
        void getUserList(onFinishedListener onFinishedListener);    // Presenter가 Model에게 데이터 요청
    }

    // View - Presenter 연결 interface - Presenter 구현 / View 호출
    interface Presenter {
        void onDestroy();   // View 소멸 시 Presenter 해제 위한 메서드

        void requestDataFromServer();   // User 데이터 재요청

        // RecyclerView Adapter도 View에서 분리하기 위해 Presenter에게 위임
        void setUserAdpaterModel(UserAdapterContract.Model model);

        void setUserAdpaterView(UserAdapterContract.View view);
    }
}