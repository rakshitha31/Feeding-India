package com.example.navada.feedingindia.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.example.navada.feedingindia.R;
import com.example.navada.feedingindia.pojos.DonationDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MyDonationsFragment extends Fragment {

    private Query donationQuery;
    private long userDonationCount = 0,readCount = 0;
    private ProgressBar progressBar;
    private LinearLayout mLinearLayout;
    private ArrayList<DonationDetails> userDonationList;
    private ChildEventListener childEventListener;

    public MyDonationsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDonationList = new ArrayList<>();
        userDonationCount = 0;
        readCount = 0;
        donationQuery = FirebaseDatabase.getInstance().getReference().child("Donations").
                        orderByKey().equalTo(FirebaseAuth.getInstance().getUid());

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("Hello", "onChildAdded: "+dataSnapshot.getValue().toString());

                ObjectMapper objectMapper = new ObjectMapper();
                HashMap<String,HashMap<String,Object>> parentMap = (HashMap<String,HashMap<String,Object>>)dataSnapshot.getValue();
                Collection<HashMap<String,Object>> mCollection = parentMap.values();
                for(HashMap<String,Object> instance : mCollection)
                {
                    DonationDetails donationDetails = objectMapper.convertValue(instance,DonationDetails.class);
                    userDonationList.add(donationDetails);
                }

                readCount++;

                if(readCount == userDonationCount)
                    enableUserInteraction();
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


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_donations, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mLinearLayout = view.findViewById(R.id.my_donations_container);

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);
        donationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userDonationCount = dataSnapshot.getChildrenCount();

                if(userDonationCount == 0)
                    enableUserInteraction();
                else
                    donationQuery.addChildEventListener(childEventListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        userDonationCount = 0;
        readCount = 0;
        userDonationList.clear();
    }

    private void enableUserInteraction()
    {
        progressBar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }
}
