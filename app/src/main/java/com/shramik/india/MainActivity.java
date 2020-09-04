package com.shramik.india;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        checkPermission();

        ProfileList.setContext(getApplicationContext());
        ProfileList.load();
        String tag = "";
        String name = getIntent().getStringExtra("name");
        String mobile = getIntent().getStringExtra("mobile");
        String password = getIntent().getStringExtra("password");
        tag = getIntent().getStringExtra("id");
        Toast.makeText(this, tag, Toast.LENGTH_SHORT).show();
        if(tag.equals("Worker")) {
            if (ProfileList.ProfileListWorker.size() == 0) {
                EditProfileWorker editProfileWorker = new EditProfileWorker();
                Bundle bundle = new Bundle();
                bundle.putString("reference", "new_login");
                bundle.putString("name", name);
                bundle.putString("mobile", mobile);
                bundle.putString("password", password);
                editProfileWorker.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, editProfileWorker).commit();
            } else {
                Toast.makeText(getApplicationContext(), ProfileList.getProfileWorkerById(ProfileList.id)+"", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewWorker()).commit();
            }
        }else if(tag.equals("Employer")){
            if (ProfileList.ProfileListEmployer.size() == 0) {
                EditProfileEmployer editProfileEmployer = new EditProfileEmployer();
                Bundle bundle = new Bundle();
                bundle.putString("reference", "new_login");
                bundle.putString("password", password);
                editProfileEmployer.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, editProfileEmployer).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewEmployer()).commit();
            }
        }else{
            if(ProfileList.occp.equals("Worker")){
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewWorker()).commit();
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewEmployer()).commit();
            }
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bubble);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:
                        if(ProfileList.occp.equals("Worker")){
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewWorker()).commit();
                        }else{
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ProfileViewEmployer()).commit();
                        }
                        break;
                    case R.id.search:
                        if(ProfileList.occp.equals("Worker")){
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new SearchWorker()).commit();
                        }else{
                            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new SearchEmployer()).commit();
                        }
                        break;
                    case R.id.post:
                        if(ProfileList.occp.equals("Worker")){
                            Toast.makeText(getApplicationContext(), "Only Employer Can Access this page", Toast.LENGTH_SHORT).show();
                        }else{
                           getSupportFragmentManager().beginTransaction().replace(R.id.frame, new PostFragment()).commit();
                        }
                        break;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new Settings()).commit();
                        break;
                }
                return true;
            }
        });



    }

    void checkPermission(){
        int permission = checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if(permission!=0){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }
}