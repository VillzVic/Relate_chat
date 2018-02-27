package com.example.appzonepc2.relate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {

    private EditText statusText;
    private Button changeStatus;
    private Toolbar toolbar;
    private DatabaseReference dbReference;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        toolbar =findViewById(R.id.status_tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Change Status");
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
//        statusText.setText(getIntent().getStringExtra("Status").toString());
        String Uid = mAuth.getCurrentUser().getUid();
        dbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
        statusText = findViewById(R.id.status_edittext);
        changeStatus = findViewById(R.id.updateStatus);


        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String status = statusText.getText().toString();
                if(statusText.getText().equals("")){
                    Toast.makeText(StatusActivity.this,"Status cannot be Empty", Toast.LENGTH_LONG).show();
                }else
                uploadStatus(status);
            }
        });
    }

    private void uploadStatus(String status) {
        progressDialog.setTitle("Updating status");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        dbReference.child("user_status").setValue(status)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Intent intent = new Intent(StatusActivity.this,SettingsActivity.class);
                            startActivity(intent);
                            Toast.makeText(StatusActivity.this,"Status Updating successfully",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(StatusActivity.this, "Error updating status", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
}
