package com.example.myloginapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {


   private TextView SignUp_SignInTextView;
   private EditText Username,Email,Password;
   Boolean isSignUp = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SignUp_SignInTextView = findViewById(R.id.SignUp_SignInTextView);
        Username = findViewById(R.id.editUsername);
        Button button = findViewById(R.id.button);
        Email = findViewById(R.id.editEmail);
        Password = findViewById(R.id.editPassword);
        setTitle("Login Page");

        if(savedInstanceState!=null){
            Username.setText(savedInstanceState.getString("Username"));
            Email.setText(savedInstanceState.getString("Email"));
            Password.setText(savedInstanceState.getString("Username"));
        }
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this, FriendsActivity.class));
            finish();
        }

        button.setOnClickListener(v -> {
            if(Email.getText().toString().isEmpty()||Password.getText().toString().isEmpty()){
                if (isSignUp&&Username.getText().toString().isEmpty()){
                    Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this,"something went Wrong",Toast.LENGTH_LONG).show();
                    return;
                }

            }
            else{
            if(isSignUp){
                if(!Username.getText().toString().isEmpty()) {
                    handleSignUp();
                }
                else{
                    Toast.makeText(this,"Username cannot be blank!!",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
            else{
               // FirebaseAuth.getInstance().email try put isEmail verified here
                handleLogin();

            }}
        });

        SignUp_SignInTextView.setOnClickListener(v -> {
            if(isSignUp){
                isSignUp= false;
                Username.setVisibility(View.GONE);
                SignUp_SignInTextView.setText(getString(R.string.SignUp_TextView));
                button.setText(getString(R.string.signInBtn));}
            else{
                isSignUp = true;
                Username.setVisibility(View.VISIBLE);
                SignUp_SignInTextView.setText(getString(R.string.SignIn_TextView));
                button.setText(getString(R.string.signUpBtn));
            }});
    }

    public void onSaveInstanceState(Bundle bundle){
        bundle.putString("Email", Email.getText().toString());
        bundle.putString("Password",Password.getText().toString());
        bundle.putString("Username", Username.getText().toString());
    }
    private void handleLogin(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(Email.getText().toString(),Password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            .this, FriendsActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }});   }
    private void handleSignUp(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(Email.getText().toString(),Password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("user"+ FirebaseAuth.getInstance().getCurrentUser()
                            .getUid()).setValue(new User(Username.getText().toString(),Email.getText().toString(),""));
                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Account Creation Success, Verify Email", Toast.LENGTH_SHORT).show();
                        }});
                }
                else{
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }});    }






}