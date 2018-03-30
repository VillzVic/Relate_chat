package com.example.appzonepc2.relate.Application;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by appzonepc2 on 06/03/2018.
 */

//for offline functionality
public class relateOffline extends Application {

    private DatabaseReference usersReference;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //load picture offline -Picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser !=null){ //if user is logged first
            String online_uid = mAuth.getCurrentUser().getUid();

            usersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_uid);

            usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    usersReference.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP ); //if the user has disconnected, set the value to false

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
