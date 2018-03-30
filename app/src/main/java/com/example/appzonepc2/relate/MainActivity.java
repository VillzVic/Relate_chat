package com.example.appzonepc2.relate;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.appzonepc2.relate.Adapters.tabPagerAdapter;
import com.example.appzonepc2.relate.Fragment.ChatFragment;
import com.example.appzonepc2.relate.Fragment.FriendsFragment;
import com.example.appzonepc2.relate.Fragment.RequestFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

//todo: there should be transitions from one page to another
//todo there should be options to delete account
//todo check if account already exists
//todo: find a way to abstract all your network call in a class
//todo: find a way to make this code modular
//todo: shima recyclerview
public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    android.support.v7.widget.Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    FirebaseUser currentUser;
    DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            String uid = mAuth.getCurrentUser().getUid();

            userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        }


        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Relate");

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewPagerr);

        tabPagerAdapter pagerAdapter = new tabPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new RequestFragment(), "Request");
        pagerAdapter.addFragment(new ChatFragment(), "Chat");
        pagerAdapter.addFragment(new FriendsFragment(), "Friends");

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    //check whether the user is logged in so as to choose the page to display
    @Override
    protected void onStart() {
        super.onStart();

         currentUser = mAuth.getCurrentUser();

        if(currentUser == null){ //setting the offline is done by relateOffline class already
            logOutUser();
        }else if(currentUser != null){ //if user is logged in, set the login status to true
              userReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() { //when user minimizes app
        super.onStop();
        if(currentUser != null){ //if user is logged in when he minimizes, set last seen time which shows he has logged out
            userReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void logOutUser(){
        Intent startPageIntent = new Intent(this, StartPageActivity.class);
        //later redirect this guy to go the login Activity and in there, there should be a link for signUp
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startPageIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         int menuitem = item.getItemId();
        if(menuitem == R.id.main_logout_button){
            //TODO: Ask if the user is sure for signing out
            //TODO: display a progress bar
            if(currentUser!=null){
                userReference.child("online").setValue(ServerValue.TIMESTAMP); //when user logs out, set the last seen
            }
            mAuth.signOut();
            logOutUser();
        }
        else if(menuitem == R.id.all_users){
            Intent intent = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(intent);
        }else if(menuitem == R.id.main_account_settings_button){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    return true;
    }
}
