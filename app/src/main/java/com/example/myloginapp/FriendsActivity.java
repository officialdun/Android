package com.example.myloginapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<User> users;
    private ProgressBar progressBar;
    private UsersAdapter usersAdapter;
    UsersAdapter.onUserClickListener onUserClickListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    String myImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
//        if(savedInstanceState!=null){
//            savedInstanceState.put
//        }

        progressBar = findViewById(R.id.progressBar);
        users = new ArrayList<>();
        for (User useR: users){
            if (useR.equals(FirebaseAuth.getInstance().getCurrentUser())){
                users.remove(useR);
            }
        }


        recyclerView = findViewById(R.id.recycler);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUsers();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        getUsers();


        onUserClickListener = new UsersAdapter.onUserClickListener() {
            @Override
            public void onUserClicked(int position) {
                startActivity(new Intent(FriendsActivity.this, MessageActivity.class)
                        .putExtra("username_roommate",users.get(position).getUsername())
                        .putExtra("email_roommate",users.get(position).getEmail())
                        .putExtra("image_roommate",users.get(position).getProfilePicture())
                       .putExtra("my_image", myImageUrl)

                );

            }
        };

    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_item_profile){
            startActivity(new Intent(FriendsActivity.this, Profile.class));
        }
        return false;
    }

    private void getUsers(){
        users.clear();

        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    users.add(dataSnapshot.getValue(User.class));
                }
                usersAdapter = new UsersAdapter(users,FriendsActivity.this,onUserClickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
                recyclerView.setAdapter(usersAdapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);

                for(User user: users){
                    if (user.getEmail()==null){
                        Log.d("RemovedUser","I removed a user here"+user);
                        users.remove(user);
                        return;
                    }
                   if (user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                       myImageUrl = user.getProfilePicture();
                       Log.d("userLogs", user.getEmail());
                       users.remove(user);
                       return;
                   }
           }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}