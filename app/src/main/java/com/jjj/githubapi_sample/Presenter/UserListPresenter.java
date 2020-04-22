package com.jjj.githubapi_sample.Presenter;

import com.jjj.githubapi_sample.Contract.OnItemClick;
import com.jjj.githubapi_sample.Contract.UserAdapterContract;
import com.jjj.githubapi_sample.Contract.UserListContract;
import com.jjj.githubapi_sample.Model.User;
import com.jjj.githubapi_sample.Model.UserListModel;

import java.util.List;

public class UserListPresenter implements UserListContract.Presenter, UserListContract.Model.onFinishedListener, OnItemClick {

    private UserListContract.View view;
    private UserListContract.Model model;

    private UserAdapterContract.Model adapterModel;
    private UserAdapterContract.View adapterView;


    public UserListPresenter(UserListContract.View view) {
        this.view = view;
        this.model = new UserListModel();
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void requestDataFromServer() {
        if (view != null) {
            view.showProgress();
        }
        model.getUserList(this);
    }

    @Override
    public void onFinished(List<User> users) {
        // 정상적 종료일 경우 Adapter 업데이트
        if (view != null) {
            view.hideProgress();
            adapterModel.setData(users);
            adapterView.notifyAdapter();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        // 비정상 실패일 경우 View에 오류 알림
        if (view != null) {
            view.onResponseFailure(t);
            view.hideProgress();
        }
    }

    @Override
    public void setUserAdpaterModel(UserAdapterContract.Model model) {
        adapterModel = model;
    }

    @Override
    public void setUserAdpaterView(UserAdapterContract.View view) {
        this.adapterView = view;
        this.adapterView.setOnClickListener(this);
    }

    @Override
    public void onItemClick(int position) {
        view.showToast(position+"번째 User 클릭");
    }
}