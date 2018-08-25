package com.android.developer.feedingindia.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.pojos.DonationDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DonordetailsPopUpActivity extends Activity{

    DatabaseReference donoationsDatabaseReference;
    private FirebaseDatabase firebaseDatabase;
    private TextView donorDetails;
    private DataSnapshot dataSnapshot;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        donorDetails=(TextView)findViewById(R.id.donordetails);

        setContentView(R.layout.activity_donordetailspopup);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        getWindow().setLayout((int)(width* .8),(int)(height* .8));


        firebaseDatabase = FirebaseDatabase.getInstance();
         donoationsDatabaseReference= firebaseDatabase.getInstance().getReference().child("Donations");

         for (DataSnapshot s:dataSnapshot.getChildren()){
             DonationDetails donations=s.getValue(DonationDetails.class);


         }



    }
}
