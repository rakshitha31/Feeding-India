package com.example.navada.feedingindia.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navada.feedingindia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.navada.feedingindia.fragments.*;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Handler mHandler;
    private String[] mFragmentTitles;
    private TextView mNavHeaderUserName;
    private static final String TAG_HOME = "Home";
    private static final String TAG_PROFILE = "Profile";
    private static final String TAG_NOTIFICATIONS = "Notifications";
    private static final String TAG_MY_DONATIONS = "My Donations";
    private static final String TAG_SPOTS = "Hunger spots I spotted";
    private static final String TAG_ABOUT_US = "About Us";
    private static int navItemIndex = 0,prevItemIndex=0;
    private static String CURRENT_TAG = TAG_HOME;
    private SharedPreferences mSharedPreferences;
    private FirebaseAuth mAuth;
    private boolean dataPersists = true;
    private DatabaseReference mDatabaseReference;
    private  ChildEventListener mChildEventListener;
    private ProgressDialog mProgressDialog;
    private AlertDialog.Builder exitBuilder,adminRequestBuilder;
    private AlertDialog adminRequestDialog;
    private static final int MAKE_ADMIN_ID = 1, POST_NOTIFICATION_ID=2;
    private Intent intent;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        final View mNavHeader = mNavigationView.getHeaderView(0);
        mNavHeaderUserName = mNavHeader.findViewById(R.id.nav_header_user_name);
        TextView mNavHeaderUserEmail = mNavHeader.findViewById(R.id.nav_header_user_email);

        mHandler = new Handler();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...\nFetching your data");

        dataPersists = true;
        CURRENT_TAG = TAG_HOME;
        navItemIndex = prevItemIndex =0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            exitBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            exitBuilder = new AlertDialog.Builder(this);
        }
        exitBuilder.setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        mSharedPreferences = this.getSharedPreferences(getPackageName(),MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        mNavHeaderUserEmail.setText(mAuth.getCurrentUser().getEmail());

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.getKey().equals(mAuth.getUid())){

                    HashMap<String,String> myMap = (HashMap<String,String>)dataSnapshot.getValue();
                    mSharedPreferences.edit().putString("name", myMap.get("name")).apply();
                    mSharedPreferences.edit().putString("email", myMap.get("email")).apply();
                    mSharedPreferences.edit().putString("password", myMap.get("password")).apply();
                    mSharedPreferences.edit().putString("doB",myMap.get("dateOfBirth")).apply();
                    mSharedPreferences.edit().putString("mobileNumber", myMap.get("mobileNumber")).apply();
                    mSharedPreferences.edit().putString("userType",myMap.get("userType")).apply();
                    mSharedPreferences.edit().remove("fetch").apply();
                    mNavHeaderUserName.setText(mSharedPreferences.getString("name", ""));
                    dataPersists = true;
                    mProgressDialog.cancel();
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    loadFragment();

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mFragmentTitles = getResources().getStringArray(R.array.fragment_titles);

        if(mSharedPreferences.getBoolean("fetch",false)) {
            dataPersists = false;
        }

        if (savedInstanceState==null && dataPersists) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadFragment();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!dataPersists) {
            mDatabaseReference.addChildEventListener(mChildEventListener);
            mProgressDialog.show();
        }
        else
            mNavHeaderUserName.setText(mSharedPreferences.getString("name", ""));

        mNavigationView.setNavigationItemSelectedListener(this);

        Query query = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final HashMap<String,Object> mMap = (HashMap<String,Object>)dataSnapshot.getValue();
                if(mMap.get("requestedToBeAdmin").equals(true)) {
                    if (adminRequestBuilder == null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            adminRequestBuilder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            adminRequestBuilder = new AlertDialog.Builder(MainActivity.this);
                        }
                        adminRequestDialog = adminRequestBuilder.setTitle("Request to be an admin")
                                .setMessage("You are requested to become an admin\nDo you want to be an admin?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        mMap.put("requestedToBeAdmin", false);
                                        mMap.put("userType", "admin");
                                        mSharedPreferences.edit().putString("userType", "admin").apply();
                                        mDatabaseReference.child(mAuth.getUid()).setValue(mMap);
                                        makeToast("Congo! You are an admin now");

                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        mMap.put("requestedToBeAdmin", false);
                                        mDatabaseReference.child(mAuth.getUid()).setValue(mMap);
                                    }
                                }).setCancelable(false).create();
                    }
                    adminRequestDialog.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onPause() {

        super.onPause();
        if(mChildEventListener!=null)
            mDatabaseReference.removeEventListener(mChildEventListener);

        if(adminRequestDialog!=null)
            adminRequestDialog.dismiss();

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawers();
        else
            exitBuilder.show();
    }


    private void loadFragment(){

        selectNavMenu();
        setToolBarTitle();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            mDrawerLayout.closeDrawers();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        mHandler.post(mPendingRunnable);
        mDrawerLayout.closeDrawers();
        invalidateOptionsMenu();

    }

    private void setToolBarTitle() {
        getSupportActionBar().setTitle(mFragmentTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        mNavigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private Fragment getFragment() {

        switch (navItemIndex) {
            case 0:
                return (new HomeFragment());
            case 1:
                return (new ProfileFragment());
            case 2:
                return (new NotificationsFragment());
            case 3:
                return (new MyDonationsFragment());
            case 4:
                return (new HungerSpotsFragment());
            case 5:
                return (new AboutUsFragment());
            default:
                return (new HomeFragment());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(mSharedPreferences.getString("userType","normal").equals("admin")) {
            menu.add(0, MAKE_ADMIN_ID, 100, "Make Admin");
            menu.add(0,POST_NOTIFICATION_ID,200,"Add Event");
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case MAKE_ADMIN_ID:
                intent = new Intent(MainActivity.this,MakeAdminActivity.class);
                startActivity(intent);
                break;

            case POST_NOTIFICATION_ID:
                break;

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.main_menu_contact_us:
                break;

            case R.id.main_menu_sign_out:
                mAuth.signOut();
                makeToast("Signed Out");
                finish();
                break;

            case R.id.main_menu_exit:
                exitBuilder.show();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.nav_menu_item_home:
                CURRENT_TAG = TAG_HOME;
                navItemIndex = 0;
                break;

            case R.id.nav_menu_item_profile:
                CURRENT_TAG = TAG_PROFILE;
                navItemIndex = 1;
                break;

            case R.id.nav_menu_item_notifications:
                CURRENT_TAG = TAG_NOTIFICATIONS;
                navItemIndex = 2;
                break;

            case R.id.nav_menu_item_donations:
                CURRENT_TAG = TAG_MY_DONATIONS;
                navItemIndex = 3;
                break;

            case R.id.nav_menu_item_hunger_spots:
                CURRENT_TAG = TAG_SPOTS;
                navItemIndex = 4;
                break;

            case R.id.nav_menu_item_about_us:
                CURRENT_TAG = TAG_ABOUT_US;
                navItemIndex = 5;
                break;

            default:
                CURRENT_TAG = TAG_HOME;
                navItemIndex=0;

        }

        mNavigationView.getMenu().getItem(prevItemIndex).setChecked(false);
        prevItemIndex = navItemIndex;
        loadFragment();
        return true;

    }

    public void makeToast(String message){
        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
    }

}
