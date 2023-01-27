package com.example.myloginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText edtMessageInput;
    private TextView txtChattingWith;
    private ImageView imageToolBar,imgSendMessage;
    private ProgressBar progressBar;
    private ArrayList<Message> messages;

    private MessageAdapter messageAdapter;



    String usernameOfTheRoomMate,emailOfRoomMate, chatRoomId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        usernameOfTheRoomMate = getIntent().getStringExtra("username_roommate");
        emailOfRoomMate = getIntent().getStringExtra("email_roommate");


        recyclerView =  findViewById(R.id.recyclerMessages);
        edtMessageInput = findViewById(R.id.edtText);
        txtChattingWith = findViewById(R.id.txtChattingWith);
        progressBar = findViewById(R.id.progressMessages);
        imageToolBar = findViewById(R.id.imageToolBar);
        imgSendMessage = findViewById(R.id.imageSend);


        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages,getIntent().getStringExtra("my_image"),
                getIntent().getStringExtra("image_roommate"), MessageActivity.this );


        txtChattingWith.setText(usernameOfTheRoomMate);



        imgSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Messages/"+chatRoomId).push()
                        .setValue(new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail()
                                ,emailOfRoomMate,edtMessageInput.getText().toString()));
                edtMessageInput.setText("");

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        Glide.with(MessageActivity.this).load(getIntent().getStringExtra("image_roommate"))
                .placeholder(R.drawable.icon_temp_image).error(R.drawable.icon_temp_image).into(imageToolBar);

       SetupChatRoom();


    }

    private void SetupChatRoom(){
        FirebaseDatabase.getInstance().getReference("user"+FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String myUsername = snapshot.getValue(User.class).getUsername();
                Log.d("username miine", myUsername);

                if (usernameOfTheRoomMate.compareTo(myUsername)>0){
                    chatRoomId = myUsername +usernameOfTheRoomMate;
                }else if(usernameOfTheRoomMate.compareTo(myUsername)==0) {
                    chatRoomId = myUsername + usernameOfTheRoomMate;
                }
                else{
                    chatRoomId = usernameOfTheRoomMate+myUsername;
                }
                attachMessageListener(chatRoomId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void attachMessageListener(String chatRoomId){
        FirebaseDatabase.getInstance().getReference("Messages/"+chatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        messages.add(dataSnapshot.getValue(Message.class));
                    }

                    messageAdapter.notifyDataSetChanged();

                recyclerView.scrollToPosition(messages.size()-1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }

}