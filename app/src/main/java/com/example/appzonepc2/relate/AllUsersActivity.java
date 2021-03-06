package com.example.appzonepc2.relate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.appzonepc2.relate.model.userListDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mReference;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        toolbar = findViewById(R.id.all_users_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_all_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        mReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mReference.keepSynced(true);




    }



    //retrieve the user's data in real time
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<userListDetails, AllUserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<userListDetails, AllUserViewHolder>
                (userListDetails.class, R.layout.user_mock,AllUserViewHolder.class,mReference) {


            @Override
            protected void populateViewHolder(AllUserViewHolder viewHolder, userListDetails model, final int position) {
                viewHolder.setUsername(model.getUser_name());
                viewHolder.setUserStatus(model.getUser_status());
                viewHolder.setUser_thumb_image(model.getUser_thumb_image(),getApplicationContext());

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String visit_user_id = getRef(position).getKey(); //  get the user id of any user  clicked
                    Intent intent = new Intent(AllUsersActivity.this,ProfileActivity.class);
                    intent.putExtra("visit_user_data",visit_user_id);
                    startActivity(intent);
                }
            });
            }


        };

//        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    public static class AllUserViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public AllUserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUsername(String user_name){
            TextView textview = mView.findViewById(R.id.all_users_username);
            textview.setText(user_name);
        }


        public void setUserStatus(String user_status){
            TextView textView = mView.findViewById(R.id.all_users_status);
            textView.setText(user_status);
        }
        public void setUser_thumb_image(final String user_thumb_image, final Context ctx){
            final CircleImageView imageView = mView.findViewById(R.id.all_users_profile_image);
//            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.profileview) ;
//            Glide.with(ctx).load(user_thumb_image).apply(requestOptions).into(imageView);
            Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profileview)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.profileview).into(imageView);
                        }
                    });
        }

    }

}
