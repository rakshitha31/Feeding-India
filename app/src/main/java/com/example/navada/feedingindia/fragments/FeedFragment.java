package com.example.navada.feedingindia.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.navada.feedingindia.R;
import com.example.navada.feedingindia.pojos.DonationDetails;
import com.example.navada.feedingindia.pojos.HungerSpot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class FeedFragment extends Fragment {

    private DatabaseReference hungerSpotDatabaseReference,donorSpotDatabaseReference;
    private ChildEventListener hungerSpotChildEventListener,donorSpotChildEventListener;
    private ProgressBar progressBar;
    private LinearLayout mLinearLayout;
    private ArrayList<HungerSpot> hungerSpots;
    private ArrayList<DonationDetails> donations;
    private long donationCount = 0,readDonationCount = 0;
    private long hungerSpotCount = 0,readHungerSpotCount = 0;
    private boolean doneReadingDonations = false;
    private boolean doneReadingHungerSpots = false;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        hungerSpotCount = 0;
        readHungerSpotCount = 0;
        donationCount = 0;
        readDonationCount = 0;
        doneReadingHungerSpots = false;
        doneReadingDonations = false;

        hungerSpotDatabaseReference = FirebaseDatabase.getInstance().getReference().child("HungerSpots");
        donorSpotDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Donations");
        donations = new ArrayList<>();
        hungerSpots = new ArrayList<>();

        hungerSpotChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HungerSpot hungerSpot = dataSnapshot.getValue(HungerSpot.class);
                if(!hungerSpots.contains(hungerSpot))
                hungerSpots.add(hungerSpot);
                readHungerSpotCount++;
                if(readHungerSpotCount==hungerSpotCount)
                    doneReadingHungerSpots = true;

                if(doneReadingDonations && doneReadingHungerSpots)
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

        donorSpotChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ObjectMapper myMapper = new ObjectMapper();
                HashMap<String,HashMap<String,DonationDetails>> myList = (HashMap<String,HashMap<String,DonationDetails>>)dataSnapshot.getValue();
                    Collection<HashMap<String, DonationDetails>> values = myList.values();
                    for (HashMap<String, DonationDetails> instance : values) {
                        DonationDetails donationDetails = myMapper.convertValue(instance, DonationDetails.class);
                        if (!donations.contains(donationDetails))
                            donations.add(donationDetails);
                    }
                    readDonationCount++;
                    if (readDonationCount == donationCount)
                        doneReadingDonations = true;
                if (doneReadingDonations && doneReadingHungerSpots)
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
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mLinearLayout = view.findViewById(R.id.feed_container);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);

       donorSpotDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                donationCount = dataSnapshot.getChildrenCount();

                if(donationCount!=0)
                    donorSpotDatabaseReference.addChildEventListener(donorSpotChildEventListener);
                else
                    doneReadingDonations = true;

                if(doneReadingHungerSpots&&doneReadingDonations)
                    enableUserInteraction();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        hungerSpotDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                hungerSpotCount = dataSnapshot.getChildrenCount();

                 if(hungerSpotCount!=0)
                     hungerSpotDatabaseReference.addChildEventListener(hungerSpotChildEventListener);
                 else
                    doneReadingHungerSpots = true;

                if(doneReadingHungerSpots&&doneReadingDonations)
                    enableUserInteraction();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        if(donorSpotChildEventListener!=null)
        donorSpotDatabaseReference.removeEventListener(donorSpotChildEventListener);

        if(hungerSpotChildEventListener!=null)
        hungerSpotDatabaseReference.removeEventListener(hungerSpotChildEventListener);

        doneReadingDonations = false;
        doneReadingHungerSpots = false;
        readDonationCount = 0;
        readHungerSpotCount = 0;
        donations.clear();
        hungerSpots.clear();
    }

    private void enableUserInteraction()
    {
        progressBar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }
}
