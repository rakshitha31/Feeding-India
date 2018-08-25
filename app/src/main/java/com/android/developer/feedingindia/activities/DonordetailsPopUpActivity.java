package com.android.developer.feedingindia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.fragments.FeedFragment;
import com.android.developer.feedingindia.pojos.DonationDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DonordetailsPopUpActivity extends Activity{

    DatabaseReference donoationsDatabaseReference;
    private FirebaseDatabase firebaseDatabase;
    private TextView donorDetails;
    private DataSnapshot dataSnapshot;
    private Button chooseHS,cancel;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        donorDetails=(TextView)findViewById(R.id.donordetails);
        chooseHS=(Button)findViewById(R.id.chooseHS);
        cancel=(Button)findViewById(R.id.cancelbtn);

        setContentView(R.layout.activity_donordetailspopup);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width* .8),(int)(height* .8));


        firebaseDatabase = FirebaseDatabase.getInstance();
         donoationsDatabaseReference= firebaseDatabase.getInstance().getReference().child("Donations");

        //To-do:  Get Donation Details data and display//
//
        //
        //
        //
        //
        //

        chooseHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DonordetailsPopUpActivity.this,HungerSpotsMapActivity.class));
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DonordetailsPopUpActivity.this,FeedMapActivity.class));
            }
        });


    }
}
