package com.android.developer.feedingindia.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.activities.FeedMapActivity;
import com.android.developer.feedingindia.pojos.DonationDetails;
import com.android.developer.feedingindia.pojos.HungerSpot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class FeedFragment extends Fragment {

    private Query hungerSpotQuery;
    private DatabaseReference donorSpotDatabaseReference;
    private ChildEventListener hungerSpotChildEventListener,donorSpotChildEventListener;
    private ProgressBar progressBar;
    private LinearLayout mLinearLayout;
    private HashMap<String,HungerSpot> hungerSpots;
    private HashMap<String,DonationDetails> donations;
    private HashMap<String,String> pushIdToUserIdMap;
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

        hungerSpotCount = readHungerSpotCount = donationCount = readDonationCount = 0;
        doneReadingHungerSpots = doneReadingDonations = false;

        hungerSpotQuery = FirebaseDatabase.getInstance().getReference().child("HungerSpots").orderByChild("status").equalTo("validated");
        donorSpotDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Donations");
        donations = new HashMap<>();
        hungerSpots = new HashMap<>();
        pushIdToUserIdMap = new HashMap<>();

        hungerSpotChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HungerSpot hungerSpot = dataSnapshot.getValue(HungerSpot.class);
                hungerSpots.put(dataSnapshot.getKey(),hungerSpot);
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
                HashMap<String,HashMap<String,Object>> myList = (HashMap<String,HashMap<String,Object>>)dataSnapshot.getValue();
                Set mySet = myList.entrySet();
                Iterator iterator = mySet.iterator();
                while(iterator.hasNext()){
                    Map.Entry myMapEntry =(Map.Entry) iterator.next();
                    DonationDetails donationDetails = myMapper.convertValue(myMapEntry.getValue(), DonationDetails.class);
                    if(!donationDetails.isCanDonate()) {
                        donations.put(myMapEntry.getKey().toString(), donationDetails);
                        pushIdToUserIdMap.put(myMapEntry.getKey().toString(),dataSnapshot.getKey());
                    }
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

        hungerSpotQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                hungerSpotCount = dataSnapshot.getChildrenCount();

                if(hungerSpotCount!=0)
                    hungerSpotQuery.addChildEventListener(hungerSpotChildEventListener);
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
            hungerSpotQuery.removeEventListener(hungerSpotChildEventListener);

        doneReadingDonations = doneReadingHungerSpots = false;
        readDonationCount = readHungerSpotCount = 0;
        donations.clear();
        hungerSpots.clear();
        pushIdToUserIdMap.clear();
    }

    private void enableUserInteraction()
    {
        progressBar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);

        //Intent to FeedMapActivity to display map having donorspots
        Intent intent=new Intent(getActivity(),FeedMapActivity.class);
    }

//=====================================================================
//    Function to be used when a user agrees to deliver
//=====================================================================
//    private void agreeToDeliver(String pushId){
//
//        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().
//                getReference().child("Donations").child(pushIdToUserIdMap.get(pushId)).child(pushId);
//
//        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                HashMap<String,Object> myMap = (HashMap<String, Object>) dataSnapshot.getValue();
//                myMap.put("status","picked");
//                mDatabaseReference.updateChildren(myMap);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

}