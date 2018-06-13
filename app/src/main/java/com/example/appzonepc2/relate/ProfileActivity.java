package com.example.appzonepc2.relate;

import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView username, userstatus;
    private Button sendFriendRequestBtn, declineFriendRequestBtn;
    private DatabaseReference databaseReference;
    private DatabaseReference friendRequestReference;
    private FirebaseAuth mAuth;
    private String sender_user_id;
    private String receiver_user_id;
    private DatabaseReference friendReference;
    private DatabaseReference notificationReference;

    private String CURRENT_STATE = "not_friends"; //kry

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView = findViewById(R.id.userImage);
        username = findViewById(R.id.profile_username);
        userstatus = findViewById(R.id.profile_userstatus);
        sendFriendRequestBtn = findViewById(R.id.friend_request);
        declineFriendRequestBtn = findViewById(R.id.decline_friend_request);

        mAuth = FirebaseAuth.getInstance();

        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notification_Request");
        notificationReference.keepSynced(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);
        //get the user id from the activity that launches this activity
         receiver_user_id = getIntent().getStringExtra("visit_user_data");



        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        friendRequestReference.keepSynced(true);
        friendReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendReference.keepSynced(true);

        //from the receiver user id, retrieve the user's details
        databaseReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                final String image = dataSnapshot.child("user_image").getValue().toString();

                username.setText(name);
                userstatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profileview).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.profileview).into(imageView);
                    }
                });


                //handle the text that the request button should show based on if request has been sent or not //handle it when the page is loaded
                //At a point sender will become receiver and vice versa,its negates
                friendRequestReference.child(sender_user_id) //if the users are friends, freinds request fragment would be deleted
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //if request has been sent, friends request node

                                  if(dataSnapshot.hasChild(receiver_user_id)){
                                      //At a point sender will become receiver and vice versa,its negates
                                      String reg_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();
                                      //This is key
                                      if(reg_type.equals("sent")) //if sender logs in
                                      {
                                          CURRENT_STATE = "request_sent";
                                          sendFriendRequestBtn.setText("Cancel Friend Request");

                                          declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                          declineFriendRequestBtn.setEnabled(false);

                                      }else if(reg_type.equals("received")){ //if the receiver logs in

                                          CURRENT_STATE = "request_received";
                                          sendFriendRequestBtn.setText("Accept Friend Request");

                                          declineFriendRequestBtn.setVisibility(View.VISIBLE); //decline button should be visible
                                          declineFriendRequestBtn.setEnabled(true);

                                          declineFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                   declineFriendRequestButton();
                                              }
                                          });

                                      }
                                  }
                               else{ // if the users  are friends, friends node
                                  friendReference.child(sender_user_id)
                                          .addListenerForSingleValueEvent(new ValueEventListener() {
                                              @Override
                                              public void onDataChange(DataSnapshot dataSnapshot) {
                                                  if(dataSnapshot.hasChild(receiver_user_id)){
                                                      CURRENT_STATE = "friends";
                                                      sendFriendRequestBtn.setText("Unfriend");

                                                      declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                      declineFriendRequestBtn.setEnabled(false);
                                                  }
                                              }

                                              @Override
                                              public void onCancelled(DatabaseError databaseError) {

                                              }
                                          });
                              }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //handle friend request
        sender_user_id = mAuth.getCurrentUser().getUid();

        declineFriendRequestBtn.setVisibility(View.INVISIBLE);
        declineFriendRequestBtn.setEnabled(false);


        if(!sender_user_id.equals(receiver_user_id)){
            //same button handling different things.
            sendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFriendRequestBtn.setEnabled(false); // after doing the operations, u can enable the button

                    if(CURRENT_STATE.equals("not_friends")){

                        SendFriendRequestToAPerson();

                    }else if(CURRENT_STATE.equals("request_sent")){

                        cancelFriendRequest();

                    }else if(CURRENT_STATE.equals("request_received")){

                            AcceptFriendRequest();

                    }else if(CURRENT_STATE.equals("friends")){
                        unFriend();
                    }
                }
            });
        }else{

            sendFriendRequestBtn.setVisibility(View.INVISIBLE);
            declineFriendRequestBtn.setVisibility(View.INVISIBLE);
        }
    }




    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(CURRENT_STATE.equals("not_friends")){

            sendFriendRequestBtn.setText("Send Friend Request");

        }else if(CURRENT_STATE.equals("request_sent")){

            sendFriendRequestBtn.setText("Cancel Friend Request");

        }else if(CURRENT_STATE.equals("request_received")){

            sendFriendRequestBtn.setText("Accept friend Request");

        }else if(CURRENT_STATE.equals("friends")){

            sendFriendRequestBtn.setText("Unfriend");
        }
    }

    private void SendFriendRequestToAPerson() {
        //send the request
        friendRequestReference.child(sender_user_id).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //to notify the receiver
                            friendRequestReference.child(receiver_user_id).child(sender_user_id)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                HashMap<String, String> notificationData = new HashMap<String, String>();
                                                notificationData.put("from",sender_user_id);
                                                notificationData.put("type", "request");

                                                notificationReference.child(receiver_user_id).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                               if(task.isSuccessful()){
                                                                   Toast.makeText(getApplicationContext(),"Request Sent Successfully",Toast.LENGTH_LONG).show();
                                                                   sendFriendRequestBtn.setEnabled(true);
                                                                   CURRENT_STATE = "request_sent";
                                                                   sendFriendRequestBtn.setText("Cancel Friend request");

                                                                   declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                                   declineFriendRequestBtn.setEnabled(false);
                                                               }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void cancelFriendRequest() {
        //when request is sent, delete the friend request and change button text
        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendFriendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestBtn.setText("Send Friend Request");

                                                declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                declineFriendRequestBtn.setEnabled(false);

                                               notificationReference.child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        sendFriendRequestBtn.setEnabled(true);
                                                        CURRENT_STATE = "not_friends";
                                                        sendFriendRequestBtn.setText("Send Friend Request");

                                                        declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                        declineFriendRequestBtn.setEnabled(false);
                                                    }
                                                   }
                                               });
                                            }

                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest() {
        Calendar calender = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        final String date = simpleDateFormat.format(calender.getTime());
        //sender and receiver became friends on this date
        friendReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(date)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(date)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //we have to remove the friend request
                                        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                sendFriendRequestBtn.setEnabled(true);
                                                                                CURRENT_STATE = "friends";
                                                                                sendFriendRequestBtn.setText("Unfriend");

                                                                                declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                                                declineFriendRequestBtn.setEnabled(false);
                                                                            }

                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                });

                    }
                });
    }

    private void unFriend() {
        friendReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendFriendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                Toast.makeText(getApplicationContext(), "You are no longer friends ",Toast.LENGTH_LONG).show();
                                                sendFriendRequestBtn.setText("Send Friend Request");

                                                declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                declineFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void declineFriendRequestButton() {
        friendRequestReference.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendRequestReference.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendFriendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestBtn.setText("Send Friend Request");

                                                declineFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                declineFriendRequestBtn.setEnabled(false);
                                            }

                                        }
                                    });
                        }
                    }
                });
    }
}

