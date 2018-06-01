package com.example.appzonepc2.relate.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appzonepc2.relate.ConversationScreen;
import com.example.appzonepc2.relate.FirebaseMessagingService;
import com.example.appzonepc2.relate.ProfileActivity;
import com.example.appzonepc2.relate.R;
import com.example.appzonepc2.relate.model.request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by appzonepc2 on 15/02/2018.
 */

public class RequestFragment extends android.support.v4.app.Fragment {
    private RecyclerView requestList;
    private DatabaseReference friendRequestReference;
    private DatabaseReference friendsReference;
    private DatabaseReference friendsRefRequest;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;
    private String online_user_id;

    View view;

    public RequestFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.requestfragment,container,false);
        requestList = view.findViewById(R.id.request_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendsReference.keepSynced(true);
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(online_user_id);
        friendRequestReference.keepSynced(true);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.keepSynced(true );
        friendsRefRequest = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

        requestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        requestList.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<request, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<request, RequestViewHolder>
                (request.class, R.layout.friend_request, RequestViewHolder.class, friendRequestReference) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, request model, final int position) {

                final String list_user_id = getRef(position).getKey();

                DatabaseReference reference = getRef(position).child("request_type").getRef();

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       if(dataSnapshot.exists()){
                           String request_type = dataSnapshot.getValue().toString();

                           if(request_type.equals("received")){
                               userReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(final DataSnapshot dataSnapshot) {
                                       final String user_name = dataSnapshot.child("user_name").getValue().toString();
                                       String user_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                                       viewHolder.setUserName(user_name);
                                       viewHolder.setUserImage(user_image, getContext());
                                       Button request_sent_btn = viewHolder.myView.findViewById(R.id.accept_button);
                                       request_sent_btn.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               AcceptFriendRequest(online_user_id,list_user_id);
                                           }
                                       });
                                       Button cancelButton = viewHolder.myView.findViewById(R.id.decline_button);
                                       cancelButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               cancelFriendRequest(online_user_id, list_user_id);
                                           }
                                       });

//                                       viewHolder.myView.setOnClickListener(new View.OnClickListener() {
//                                           @Override
//                                           public void onClick(View v) {
//                                               CharSequence options[] = new CharSequence[]
//                                                       {
//                                                               "Accept Friend Request", "Cancel Friend Request"
//                                                       };
//
//                                               AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                               builder.setTitle("Friend request options");
//
//                                               builder.setItems(options, new DialogInterface.OnClickListener() {
//                                                   @Override
//                                                   public void onClick(DialogInterface dialog, int position) {
//                                                       if(position == 0){
//                                                           AcceptFriendRequest(online_user_id,list_user_id);
//                                                       }
//                                                       if(position == 1){
//                                                           cancelFriendRequest(online_user_id, list_user_id);
//                                                       }
//                                                   }
//                                               });
//
//                                               builder.show();
//                                           }
//                                       });
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               });
                           }else if(request_type.equals("sent")){

                               Button request_sent_btn = viewHolder.myView.findViewById(R.id.accept_button);
                               request_sent_btn.setText("Request sent");


                               Button request_received_btn = viewHolder.myView.findViewById(R.id.decline_button);
                               request_received_btn.setText("Cancel");

                               userReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       String user_name = dataSnapshot.child("user_name").getValue().toString();
                                       String user_image = dataSnapshot.child("user_thumb_image").getValue().toString();

                                       viewHolder.setUserName(user_name);
                                       viewHolder.setUserImage(user_image, getContext());
                                       Button cancelButton = viewHolder.myView.findViewById(R.id.decline_button);
                                       cancelButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               cancelFriendRequest(online_user_id, list_user_id);
                                           }
                                       });
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               });
                           }
                       }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        requestList.setAdapter(firebaseRecyclerAdapter);
    }

    private void AcceptFriendRequest(final String sender_user_id, final String receiver_user_id ) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        final String date = simpleDateFormat.format(calendar.getTime());
        //sender and receiver became friends on this date
        friendsReference.child(sender_user_id).child(receiver_user_id).child("date").setValue(date)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendsReference.child(receiver_user_id).child(sender_user_id).child("date").setValue(date)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //we have to remove the friend request
                                        friendsRefRequest.child(sender_user_id).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            friendsRefRequest.child(receiver_user_id).child(sender_user_id).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                Toast.makeText(getContext(), "Friends Request Accepted", Toast.LENGTH_SHORT).show();
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

    private void cancelFriendRequest(final String sender_user_id, final String receiver_user_id) {
        //when request is sent, delete the friend request and change button text
        friendsRefRequest.child(sender_user_id).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            friendsRefRequest.child(receiver_user_id).child(sender_user_id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getContext(), "Friend Request cancelled", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        }
                    }
                });
    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        View myView;
        TextView userName;
        CircleImageView imageView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            myView = itemView;

            userName = myView.findViewById(R.id.request_user);
            imageView = myView.findViewById(R.id.request_profile_image);
        }

        public void setUserName(String username){
            userName.setText(username);
        }

        public void setUserImage(final String userimage, final Context ctx){
            Picasso.with(ctx).load(userimage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profileview)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(userimage).placeholder(R.drawable.profileview).into(imageView);
                        }
                    });
        }
    }
}
