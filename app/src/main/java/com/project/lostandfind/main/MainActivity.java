package com.project.lostandfind.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.project.lostandfind.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private AddPostFragment addPostFragment;
    private BottomNavigationView bottom_navigation;

    private FrameLayout homeFrame, profileFrame, addPostFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermissions();
        findViews();
        initViews();

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home:
                        homeFrame.setVisibility(View.VISIBLE);
                        addPostFrame.setVisibility(View.INVISIBLE);
                        profileFrame.setVisibility(View.INVISIBLE);

                        break;
                    case R.id.menu_add_post:
                        homeFrame.setVisibility(View.INVISIBLE);
                        addPostFrame.setVisibility(View.VISIBLE);
                        profileFrame.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.menu_profile:
                        homeFrame.setVisibility(View.INVISIBLE);
                        addPostFrame.setVisibility(View.INVISIBLE);
                        profileFrame.setVisibility(View.VISIBLE);
                        break;
                }
                return true;
            }
        });
    }

    private void askPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 102);
        }
    }

    private void initViews() {
        homeFragment = new HomeFragment();
        homeFragment.setActivity(MainActivity.this);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_home, homeFragment).commit();

        addPostFragment = new AddPostFragment();
        addPostFragment.setActivity(MainActivity.this);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_add_post, addPostFragment).commit();

        profileFragment = new ProfileFragment();
        profileFragment.setActivity(MainActivity.this);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_profile, profileFragment).commit();

        profileFrame.setVisibility(View.INVISIBLE);
        addPostFrame.setVisibility(View.INVISIBLE);
    }


    public void findViews(){
        bottom_navigation = findViewById(R.id.bottom_navigation);
        homeFrame = findViewById(R.id.main_frame_home);
        addPostFrame = findViewById(R.id.main_frame_add_post);
        profileFrame = findViewById(R.id.main_frame_profile);

    }
}