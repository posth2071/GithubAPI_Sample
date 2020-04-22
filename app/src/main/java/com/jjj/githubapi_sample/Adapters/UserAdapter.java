package com.jjj.githubapi_sample.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jjj.githubapi_sample.R;
import com.jjj.githubapi_sample.Contract.OnItemClick;
import com.jjj.githubapi_sample.Contract.UserAdapterContract;
import com.jjj.githubapi_sample.Model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements UserAdapterContract.View, UserAdapterContract.Model {
    private final String TAG = "UserAdapter";
    private Context context;
    private OnItemClick onItemClick;
    private List<User> users;

    public UserAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
//        holder.image.setText(user.getImage());
        Glide.with(context)
                .load(user.getImage())
                .apply(new RequestOptions().circleCrop())
                .into(holder.image);

        holder.name.setText(user.getLogin());
        holder.location.setText(user.getLocation());
        holder.blog.setText(user.getBlog());
        holder.follower.setText(String.valueOf(user.getFollowers()));
        holder.following.setText(String.valueOf(user.getFollowing()));
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, location, blog, follower, following;
        ImageView image;

        public ViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.user_name);
            location = view.findViewById(R.id.user_location);
            blog = view.findViewById(R.id.user_blog);
            follower = view.findViewById(R.id.user_follower);
            following = view.findViewById(R.id.user_following);

            image = view.findViewById(R.id.user_image);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onItemClick(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public void setOnClickListener(OnItemClick clickListener) {
        this.onItemClick = clickListener;
    }

    @Override
    public void setData(List<User> users) {
        this.users = users;
    }

    @Override
    public User user(int position) {
        return users.get(position);
    }
}