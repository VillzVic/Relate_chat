package com.example.appzonepc2.relate.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appzonepc2.relate.FirebaseMessagingService;
import com.example.appzonepc2.relate.R;
import com.example.appzonepc2.relate.model.request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by appzonepc2 on 15/02/2018.
 */

public class RequestFragment extends android.support.v4.app.Fragment {
    private RecyclerView requestList;
    private DatabaseReference friendRequestReference;
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
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(online_user_id);
        friendRequestReference.keepSynced(true);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.keepSynced(true );

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
            protected void populateViewHolder(final RequestViewHolder viewHolder, request model, int position) {

                final String user_list_id = getRef(position).getKey();
                userReference.child(user_list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String user_name = dataSnapshot.child("user_name").toString();
                        String user_image = dataSnapshot.child("user_thumb_image").toString();

                        viewHolder.setUserName(user_name);
                        viewHolder.setUserImage(user_image, getContext());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        requestList.setAdapter(firebaseRecyclerAdapter);
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
