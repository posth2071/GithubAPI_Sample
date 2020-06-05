package com.jjj.githubapi_sample.Presenter;

import com.jjj.githubapi_sample.Contract.OnItemClick;
import com.jjj.githubapi_sample.Contract.UserAdapterContract;
import com.jjj.githubapi_sample.Contract.UserListContract;
import com.jjj.githubapi_sample.Model.User;
import com.jjj.githubapi_sample.Model.UserListModel;

import java.util.List;

public class UserListPresenter implements UserListContract.Presenter, UserListContract.Model.onFinishedListener, OnItemClick {

    private UserListContract.View view;		            // View Contract 구현체
    private UserListContract.Model model;	            // Model Contract 구현체

    private UserAdapterContract.Model adapterModel;	    // Adapter_Model Contract 구현체
    private UserAdapterContract.View adapterView;	    // Adapter_View Contract 구현체

    // Presenter 생성자, View Contract 구현체 함수인수로 전달
    public UserListPresenter(UserListContract.View view) {
        this.view = view;
        this.model = new UserListModel();	// Model Contract 구현체 선언, UserListModel이 해당 Contract 인터페이스 상속 필요
    }

    // View-Presenter 추상메서드 onDestory() 정의 (View에서 사용)
    @Override
    public void onDestroy() {
        view = null;
    }

    // View-Presenter 추상메서드 requestDataFromServer() 정의 (View에서 사용)
    @Override
    public void requestDataFromServer() {
        if (view != null) {	// View가 소멸된 상태인지?
            view.showProgress();
        }
        model.getUserList(this);
    }

    // Presenter-Model 추상메서드 onFinished() 정의 (Model에서 사용)
    // Request 모두 정상적으로 완료되었을 경우
    @Override
    public void onFinished(List<User> users) {
        // View 존재(소멸) 체크
        if (view != null) {
            view.hideProgress();		    // 진행바(프로그래스바) 표시
            adapterModel.setData(users);	// Adatper에 Data 추가
            adapterView.notifyAdapter();	// RecyclerView 갱신
        }
    }

    // Presenter-Model 추상메서드 onFailure() 정의 (Model에서 사용)
    // Request 요청 실패할 경우
    @Override
    public void onFailure(Throwable t) {
        // View 존재(소멸) 체크
        if (view != null) {
            view.onResponseFailure(t);	// View 통신실패 이벤트 호출
            view.hideProgress();	// 진행바(프로그래스바) 숨기기
        }
    }

    // View-Presenter 추상메서드 정의 (View에서 사용)
    @Override
    public void setUserAdpaterModel(UserAdapterContract.Model model) {
        adapterModel = model;	// Adapter.Model 할당
    }

    // View-Presenter 추상메서드 정의 (View에서 사용)
    @Override
    public void setUserAdpaterView(UserAdapterContract.View view) {
        this.adapterView = view;	// Adatper.View 할당
        this.adapterView.setOnClickListener(this); // Adapter.View에 클릭이벤트 리스너 할당
    }

    // OnItemClick 인터페이스 추상메서드 정의 (Adapter에서 사용)
    @Override
    public void onItemClick(int position) {
        view.showToast(position+"번째 User 클릭"); // Adapter에서 Item 클릭이벤트로 해당 Item index 전달
    }
}