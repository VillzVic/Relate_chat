package com.example.appzonepc2.relate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button loginButton;
    FirebaseAuth mAuth;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.loginemail);
        password = findViewById(R.id.loginpass);
        loginButton = findViewById(R.id.loginButton);
        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = email.getText().toString();
                String userPassword = password.getText().toString();

                logInUser(emailAddress, userPassword);
            }
        });
    }

    private void logInUser(String emailAddress, String userPassword) {
        mProgressDialog.setTitle("Login");
        mProgressDialog.setMessage("Please wait while we verify your credentials");
        mProgressDialog.show();

        if(TextUtils.isEmpty(emailAddress) || TextUtils.isEmpty(userPassword)){
            Toast.makeText(this,"Email or password cannot be empty",Toast.LENGTH_LONG).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(emailAddress,userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            //check for internet connection
//                            else if(){
//
//                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Wrong Email Address or Password", Toast.LENGTH_LONG).show();
                            }
                            mProgressDialog.dismiss();
                        }
                    });
        }
    }
}
