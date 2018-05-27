package com.example.appzonepc2.relate.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appzonepc2.relate.ConversationScreen;
import com.example.appzonepc2.relate.R;
import com.example.appzonepc2.relate.model.chat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by appzonepc2 on 15/02/2018.
 */

public class ChatFragment extends Fragment {
    String message, date;
    View mView;
    private RecyclerView chatRecyclerView;
    private DatabaseReference friendsReference;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;
    private String online_user_id;

    public ChatFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.chatfragment, container, false);
        chatRecyclerView = mView.findViewById(R.id.chat_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        friendsReference.keepSynced(true);

        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.keepSynced(true);

        chatRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        chatRecyclerView.setLayoutManager(linearLayoutManager);
        return mView;
    }


    @Override
    public void onStart() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yy");
        final String date = dateFormat.format(calendar.getTime());

        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new messageReceiver(), new IntentFilter("last_message"));

        FirebaseRecyclerAdapter<chat, ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<chat, ChatViewHolder>
                (chat.class,R.layout.chat_model,ChatViewHolder.class,friendsReference) {
            @Override
            protected void populateViewHolder(final ChatViewHolder viewHolder, chat model, int position) {
//                viewHolder.setLastMessage(message);
//                viewHolder.setDate(date);

                final String list_user_id = getRef(position).getKey(); //most important thing here

                userReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String username = dataSnapshot.child("user_name").getValue().toString();
                        final String userImage = String.valueOf(dataSnapshot.child("user_thumb_image").getValue());

                        viewHolder.setUsername(username);
                        viewHolder.setUserImage(userImage, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(dataSnapshot.child("online").exists()){ //whether its true or last seen time that is there.. add validation
                                    Intent intent = new Intent(getContext(), ConversationScreen.class);
                                    intent.putExtra("visit_user_id", list_user_id);
                                    intent.putExtra("user_name", username);
                                    startActivity(intent);
                                }
                                else{
                                    userReference.child(list_user_id).child("online").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(getContext(), ConversationScreen.class);
                                            intent.putExtra("visit_user_id", list_user_id);
                                            intent.putExtra("user_name", username);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        chatRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
//
//
//

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(new messageReceiver());
    }

//
    public class messageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getStringExtra("message");
            date = intent.getStringExtra("date"); //date of last message
        }
    }
//
//
    private static class ChatViewHolder extends RecyclerView.ViewHolder{
        View mView; //if you make it static, it will retrieve only one user, each user won't have instances of the the view

        public ChatViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setLastMessage(String message) {
            TextView textView = mView.findViewById(R.id.recent_message);
            textView.setText(message);
        }

        public void setDate(String date){
            TextView dateView = mView.findViewById(R.id.date);
            dateView.setText(date);
        }

        public void setUsername(String username){
            TextView userTextview = mView.findViewById(R.id.chat_username);
            userTextview.setText(username);
        }

        public void setUserImage(final String userimage, final Context ctx){
            final CircleImageView imageView = mView.findViewById(R.id.chat_profile_image);

            Picasso.with(ctx).load(userimage).placeholder(R.drawable.profileview).networkPolicy(NetworkPolicy.OFFLINE)
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


