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
//todo: there should be transitions from one page to another
//todo there should be options to delete account
//todo check if account already exists
public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    android.support.v7.widget.Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
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

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            logOutUser();
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
