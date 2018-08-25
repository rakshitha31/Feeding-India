package com.android.developer.feedingindia.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.android.developer.feedingindia.pojos.DonationDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MyDeliveriesFragment extends Fragment {

    private DatabaseReference donationDatabaseReference;
    private ChildEventListener childEventListener;
    private long donationCount = 0 , readDonationCount = 0;
    private ProgressBar progressBar;
    private LinearLayout mLinearLayout;
    private HashMap<String,DonationDetails> userDeliveries;
    private HashMap<String,String> pushIdToUserIdMap;
    private String userName;
    private SharedPreferences mSharedPreferences;

    public MyDeliveriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        donationCount = readDonationCount = 0;
        userDeliveries = new HashMap<>();
        pushIdToUserIdMap = new HashMap<>();
        donationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Donations");
        mSharedPreferences = getActivity().getSharedPreferences("com.android.developer.feedingindia", Context.MODE_PRIVATE);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ObjectMapper myMapper = new ObjectMapper();
                HashMap<String,HashMap<String,Object>> myList = (HashMap<String,HashMap<String,Object>>)dataSnapshot.getValue();
                Set mySet = myList.entrySet();
                Iterator iterator = mySet.iterator();
                while(iterator.hasNext()){
                    Map.Entry myMapEntry =(Map.Entry) iterator.next();
                    DonationDetails donationDetails = myMapper.convertValue(myMapEntry.getValue(), DonationDetails.class);
                    if(donationDetails.getDeliverer().equals(userName)) {
                        userDeliveries.put(myMapEntry.getKey().toString(), donationDetails);
                        pushIdToUserIdMap.put(myMapEntry.getKey().toString(),dataSnapshot.getKey());
                    }
                }

                readDonationCount++;
                if (readDonationCount == donationCount)
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
        View view = inflater.inflate(R.layout.fragment_my_deliveries, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mLinearLayout = view.findViewById(R.id.deliveries_container);

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();

        userName = mSharedPreferences.getString("name","");
        progressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);

        donationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                donationCount = dataSnapshot.getChildrenCount();

                if(donationCount == 0)
                    enableUserInteraction();
                else
                    donationDatabaseReference.addChildEventListener(childEventListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        donationCount = readDonationCount = 0;
        userDeliveries.clear();
        pushIdToUserIdMap.clear();
    }

    private void enableUserInteraction()
    {
        progressBar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    private void onClickDelivered(String pushId){

        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().
                getReference().child("Donations").child(pushIdToUserIdMap.get(pushId)).child(pushId);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> myMap = (HashMap<String, Object>) dataSnapshot.getValue();
                myMap.put("status","delivered");
                mDatabaseReference.updateChildren(myMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void onClickCannotDeliver(String pushId) {

        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().
                getReference().child("Donations").child(pushIdToUserIdMap.get(pushId)).child(pushId);

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> myMap = (HashMap<String, Object>) dataSnapshot.getValue();
                myMap.put("deliverer","none");
                myMap.put("status","pending");
                if(myMap.get("canDonate").equals(true))
                    myMap.put("canDonate",false);
                mDatabaseReference.updateChildren(myMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}