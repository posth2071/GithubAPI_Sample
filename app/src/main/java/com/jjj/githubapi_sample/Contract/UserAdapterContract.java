package com.jjj.githubapi_sample.Contract;

import com.jjj.githubapi_sample.Model.User;

import java.util.List;

public interface UserAdapterContract {
    interface View {
        void notifyAdapter();

        void setOnClickListener(OnItemClick clickListener);
    }

    interface Model {
        void setData(List<User> users);

        User user(int position);
    }
}