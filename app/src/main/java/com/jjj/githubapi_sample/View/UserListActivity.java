package com.jjj.githubapi_sample.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjj.githubapi_sample.Adapters.UserAdapter;
import com.jjj.githubapi_sample.Contract.UserListContract;
import com.jjj.githubapi_sample.Presenter.UserListPresenter;
import com.jjj.githubapi_sample.R;

public class UserListActivity extends AppCompatActivity implements UserListContract.View, View.OnClickListener {
    private static final String TAG = "UserListActivity";

    private UserListPresenter presenter;
    private UserAdapter usersAdapter;

    private RecyclerView rvUsersList;
    private Button btn_rest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }

    public void initUI() {
        btn_rest = findViewById(R.id.btn_rest);
        btn_rest.setOnClickListener(this);

        usersAdapter = new UserAdapter(this);

        rvUsersList = findViewById(R.id.recyclerView);
        rvUsersList.setLayoutManager(new LinearLayoutManager(this));
        rvUsersList.setAdapter(usersAdapter);

        presenter = new UserListPresenter(this);
        presenter.setUserAdpaterModel(usersAdapter);
        presenter.setUserAdpaterView(usersAdapter);

    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResponseFailure(Throwable throwable) {
        Log.d(TAG, throwable.getMessage());
        Toast.makeText(this, "Network 통신 Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_rest:
                presenter.requestDataFromServer();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}