package com.example.myloginapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder>{

    private List<User> users;
    private Context context;
    private onUserClickListener onUserClickListener;


    public UsersAdapter(List<User> users, Context context, UsersAdapter.onUserClickListener onUserClickListener) {
        this.users = users;
        this.context = context;
        this.onUserClickListener = onUserClickListener;

    }

    interface onUserClickListener{
        void onUserClicked(int position);
    }


    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_holder,parent,false);
            return new UserHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {

            holder.txtUsername.setText(users.get(position).getUsername());
        Glide.with(context).load(users.get(position).getProfilePicture()).error(R.drawable.icon_temp_image)
                .placeholder(R.drawable.icon_temp_image).into(holder.imageView);
    }

    @Override
    public int getItemCount() {

        return users.size();
    }

    class UserHolder extends RecyclerView.ViewHolder{
        TextView txtUsername;
        ImageView imageView;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onUserClickListener.onUserClicked(getAdapterPosition());
                }
            });
            txtUsername = itemView.findViewById(R.id.txtUsername);
            imageView = itemView.findViewById(R.id.image_pro);
        }

    }
}

