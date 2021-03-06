package com.simpumind.e_tech_news.activities;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simpumind.e_tech_news.R;
import com.simpumind.e_tech_news.fragments.LibraryFragment;
import com.simpumind.e_tech_news.fragments.MyProfileFragment;
import com.simpumind.e_tech_news.fragments.SubscriptionFragment;
import com.simpumind.e_tech_news.utils.NewsService;

public class NewsMainActivity extends AppCompatActivity {

    private static final String TAG = NewsMainActivity.class.getSimpleName();
    public static final String CAT_NAME = "category";

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);


        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.home:
                                selectedFragment = new SubscriptionFragment();
                                toolbar.setTitle("Vendors");
                                break;
                            case R.id.library:
                                selectedFragment = new LibraryFragment();
                                toolbar.setTitle("Library");
                                break;
                            case R.id.profile:
                                selectedFragment = new MyProfileFragment();
                                toolbar.setTitle("My Profile");
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.framelayout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.framelayout, new SubscriptionFragment());
        transaction.commit();

        startService(new Intent(getBaseContext(), NewsService.class));
    }

}
