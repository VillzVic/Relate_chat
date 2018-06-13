package com.example.appzonepc2.relate.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appzonepc2.relate.ConversationScreen;
import com.example.appzonepc2.relate.ProfileActivity;
import com.example.appzonepc2.relate.R;
import com.example.appzonepc2.relate.model.friends;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by appzonepc2 on 15/02/2018.
 */

public class FriendsFragment extends android.support.v4.app.Fragment {


    private String online_user_id;
    private RecyclerView mRecyclerView;
    private View mView;
    private DatabaseReference friendsReference;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

         mView = inflater.inflate(R.layout.friendfragment,container,false);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        mRecyclerView = mView.findViewById(R.id.friendsList);

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        friendsReference.keepSynced(true);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.keepSynced(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<friends, FriendsViewHolder> friendsRecyclerAdapter =
                new FirebaseRecyclerAdapter  <friends, FriendsViewHolder>
                        (friends.class,R.layout.friends_mock,FriendsViewHolder.class,friendsReference){

                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, friends model, final int position) {

                        viewHolder.setDate(model.getDate());

                        //get reference to the user's id on the list and query Users to get their details for the app
                        final String list_user_id = getRef(position).getKey(); //get the user id

                        userReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                final String username = dataSnapshot.child("user_name").getValue().toString();
                                final String userImage = String.valueOf(dataSnapshot.child("user_thumb_image").getValue());

                                viewHolder.setUsername(username);
                                viewHolder.setUserImage(userImage, getContext());

                                if(dataSnapshot.hasChild("online")){
                                    String online = String.valueOf(dataSnapshot.child("online").getValue());

                                    viewHolder.setUserOnlineStatus(online);
                                }

                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CharSequence options[] = new CharSequence[]
                                                {
                                                  "View Profile", "Send Message"
                                                };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle(username);

                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int position) {
                                                if(position == 0){
                                                    Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                                    profileIntent.putExtra("visit_user_data", list_user_id);
                                                    startActivity(profileIntent);
                                                }
                                                if(position == 1){
                                                    if(dataSnapshot.child("online").exists()){ //whether its true or last seen time that is there.. add validation
                                                        Intent chatIntent = new Intent(getContext(), ConversationScreen.class);
                                                        chatIntent.putExtra("visit_user_id", list_user_id);
                                                        chatIntent.putExtra("user_name", username);
                                                        startActivity(chatIntent);
                                                    }
                                                    else{
                                                        userReference.child(list_user_id).child("online").setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Intent chatIntent = new Intent(getContext(), ConversationScreen.class);
                                                                chatIntent.putExtra("visit_user_id", list_user_id);
                                                                chatIntent.putExtra("user_name", username);
                                                                startActivity(chatIntent);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                });

                                        builder.show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                };
//    friendsRecyclerAdapter.notifyDataSetChanged(); //recent on top
        mRecyclerView.setAdapter(friendsRecyclerAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView; //if you make it static, it will retrieve only one user, each user won't have instances of the the view

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDate(String date) {
            TextView textView = mView.findViewById(R.id.friends_status);
            textView.setText("Friends Since: " +  date);
        }

        public void setUsername(String username){
            TextView userTextview = mView.findViewById(R.id.friends_username);
            userTextview.setText(username);
        }

        public void setUserImage(final String userimage, final Context ctx){
            final CircleImageView imageView = mView.findViewById(R.id.friends_profile_image);

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

        public void setUserOnlineStatus(String online){
            ImageView imageView = mView.findViewById(R.id.friends_online);

            if(online.equals("true")){
                imageView.setVisibility(View.VISIBLE);
            }else{

            }
        }

    }
}
