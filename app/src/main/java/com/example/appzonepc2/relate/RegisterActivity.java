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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email, Password;
    private Button signin;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.usernameReg);
        email = findViewById(R.id.emailReg);
        Password = findViewById(R.id.passReg);
        signin = findViewById(R.id.signInButton);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this );


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = username.getText().toString();
                String Email = email.getText().toString();
                String password = Password.getText().toString();

                RegisterAccount(name, Email, password);
            }
        });
    }

    private void RegisterAccount(final String name, final String email, String password) {
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Username Cannot be empty", Toast.LENGTH_LONG).show();
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Email cannot be empty", Toast.LENGTH_LONG).show();
        }
        //TODO: the miminum character must be 8
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_LONG).show();
        }

        else{

            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("Please wait... ");
            progressDialog.show();
            //create progressBar
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //get the user id
                                String userId = mAuth.getCurrentUser().getUid();
                                String device_token = FirebaseInstanceId.getInstance().getToken();
                                //store the data into the databasel
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                //todo: get the user id for device token here instead
                                mDatabase.child("user_name").setValue(name);
                                mDatabase.child("user_status").setValue("Hey There, am using relate");
                                mDatabase.child("user_image").setValue("default_profile");
                                mDatabase.child("device_token").setValue(device_token);
                                mDatabase.child("user_thumb_image").setValue("default_image");
                                mDatabase.child("phone_number").setValue("");
                                mDatabase.child("Address").setValue("");
                                mDatabase.child("Email").setValue(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    Toast.makeText(getApplicationContext(), "Sign Up Successful, please login ",Toast.LENGTH_LONG).show();
                                                    Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    startActivity(mainIntent);
                                                    finish();
                                                }
                                            }
                                        });
                            }else{
                                Toast.makeText(getApplicationContext(), "An error Occurred, Please Check your internet connection", Toast.LENGTH_LONG).show();
                            }

                            progressDialog.dismiss();
                        }


                    });
        }

    }
}
