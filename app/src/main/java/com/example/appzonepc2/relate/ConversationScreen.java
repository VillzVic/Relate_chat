package com.example.appzonepc2.relate;

//todo: change every network call to be on the background using belvi's style, context.StartService(intent) or AsyncTask
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appzonepc2.relate.Adapters.conversationAdapter;
import com.example.appzonepc2.relate.Utils.lastSeen;
import com.example.appzonepc2.relate.model.message_model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationScreen extends AppCompatActivity implements View.OnClickListener {
    private String receiver_userid;
    private String username;
    private Toolbar toolbar;



    private TextView user_profile_name;
    private TextView user_last_seen;
    private CircleImageView circular_image_view;
    private FirebaseAuth mAuth;
    private StorageReference messageImageRef;
    private String sender_id;
    private DatabaseReference rootref;
    private ImageView camera, send;
    private AppCompatEditText editText;
    private RecyclerView messagesList;
    private ArrayList<message_model> usermessageList;
    private conversationAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private static int Gallery_pick = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_screen);
        initToolbar();
        layoutManager = new LinearLayoutManager(this);
        usermessageList = new ArrayList<>();
        messagesList = findViewById(R.id.messages);

        rootref = FirebaseDatabase.getInstance().getReference();
        messageImageRef = FirebaseStorage.getInstance().getReference().child("message_images");  //folder


        mAdapter = new conversationAdapter(usermessageList,ConversationScreen.this);
        messagesList.setHasFixedSize(true);
        messagesList.setLayoutManager(layoutManager);
        messagesList.setAdapter(mAdapter);
        mAuth = FirebaseAuth.getInstance();
        sender_id = mAuth.getCurrentUser().getUid();

        receiver_userid = getIntent().getStringExtra("visit_user_id").toString();
        username = getIntent().getStringExtra("user_name").toString();



        camera = findViewById(R.id.camera_button);
        send = findViewById(R.id.send_button);
        editText = findViewById(R.id.inputtext);


        camera.setOnClickListener(this);


        user_profile_name = findViewById(R.id.custom_profile_name);
        user_last_seen = findViewById(R.id.custom_last_seen);
        circular_image_view = findViewById(R.id.custom_profile_image);

        fetchMessage();
        user_profile_name.setText(username);


        rootref.child("Users").child(receiver_userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String online = dataSnapshot.child("online").getValue().toString();
                final String userThumb = dataSnapshot.child("user_thumb_image").getValue().toString();

                Picasso.with(ConversationScreen.this).load(userThumb).placeholder(R.drawable.profileview).networkPolicy(NetworkPolicy.OFFLINE)
                        .into(circular_image_view, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ConversationScreen.this).load(userThumb).placeholder(R.drawable.profileview).into(circular_image_view );
                            }
                        });

                if(online.equals("true")){
                    user_last_seen.setText("online");
                }else { //value of online
                    long last_seen_time = Long.parseLong(online);

                    user_last_seen.setText(String.valueOf(lastSeen.getTimeAgo(last_seen_time,getApplicationContext())));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               sendMessage();
            }
        });


    }

    private void cameraLaunch() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, Gallery_pick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            String message_sender_ref = "Messages/" + sender_id + "/" + receiver_userid;
            String message_receiver_ref = "Messages/" + receiver_userid + "/" + sender_id;

            DatabaseReference user_message_key = rootref.child("Messages").child(sender_id).child(receiver_userid).push();

            String message_push_id = user_message_key.getKey();

            StorageReference filepath = messageImageRef.child(message_push_id+".jpg");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    String imageUrl = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(ConversationScreen.this, "Image Uploaded succesfully", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void fetchMessage() {
        rootref.child("Messages").child(sender_id).child(receiver_userid) //works in both ways
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        message_model mModel = dataSnapshot.getValue(message_model.class);
                        usermessageList.add(mModel);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(view);
    }

    private void sendMessage() {
        String message = editText.getText().toString();


        if(!TextUtils.isEmpty(message)){
            String message_sender_ref = "Messages/" + sender_id + "/" + receiver_userid;
            String message_receiver_ref = "Messages/" + receiver_userid + "/" + sender_id;

            DatabaseReference user_message_key = rootref.child("Messages").child(sender_id).child(receiver_userid).push();

            String message_push_id = user_message_key.getKey();

            Map messageTextBody = new HashMap();

            messageTextBody.put("message", message);
            messageTextBody.put("seen", false);
            messageTextBody.put("type","text");
            messageTextBody.put("time", ServerValue.TIMESTAMP);
            messageTextBody.put("from", sender_id);

            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody);
            messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

            rootref.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.d("chat_log", databaseError.getMessage().toString());
                    }

                    editText.setText("");
                }
            });
//            LocalBroadcastManager.getInstance(ConversationScreen.this).sendBroadcast(new Intent("last_message")
//                        .putExtra("message", message));

        }else{
            Toast.makeText(ConversationScreen.this,"Please write a message.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.camera_button:

                cameraLaunch();
                break;
        }
    }
}
