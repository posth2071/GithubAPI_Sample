package com.jjj.githubapi_sample.Contract;

import com.jjj.githubapi_sample.Model.User;

import java.util.List;

public interface UserListContract {
    interface Model {
        interface onFinishedListener {
            void onFinished(List<User> users);

            void onFailure(Throwable t);
        }

        void getUserList(onFinishedListener onFinishedListener);
    }

    interface View {
        void showProgress();

        void hideProgress();

        void showToast(String message);

        void onResponseFailure(Throwable throwable);

    }

    interface Presenter {
        void onDestroy();

        void requestDataFromServer();

        void setUserAdpaterModel(UserAdapterContract.Model model);

        void setUserAdpaterView(UserAdapterContract.View view);
    }
}