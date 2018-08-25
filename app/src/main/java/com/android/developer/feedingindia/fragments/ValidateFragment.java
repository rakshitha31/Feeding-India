package com.android.developer.feedingindia.fragments;

import android.location.Location;
import android.location.LocationManager;
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
import com.android.developer.feedingindia.pojos.HungerSpot;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class ValidateFragment extends Fragment {

    private ProgressBar progressBar;
    private LinearLayout mLinearLayout;
    private Query hungerSpotQuery;
    private ChildEventListener childEventListener;
    private HashMap<String,Location> mHungerSpots;
    private long hungerSpotCount,readHungerSpots = 0;

    public ValidateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHungerSpots = new HashMap<>();
        readHungerSpots = 0;

        hungerSpotQuery = FirebaseDatabase.getInstance().getReference().child("HungerSpots").
                orderByChild("status").equalTo("pending");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HungerSpot hungerSpot = dataSnapshot.getValue(HungerSpot.class);
                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(hungerSpot.getLatitude());
                location.setLongitude(hungerSpot.getLongitude());
                mHungerSpots.put(dataSnapshot.getKey(),location);

                readHungerSpots++;

                if(readHungerSpots == hungerSpotCount)
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
        View view = inflater.inflate(R.layout.fragment_validate, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        mLinearLayout = view.findViewById(R.id.validate_container);

        return view;

    }


    @Override
    public void onResume() {

        super.onResume();

        progressBar.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.INVISIBLE);

        hungerSpotQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                hungerSpotCount = dataSnapshot.getChildrenCount();
                if(hungerSpotCount == 0)
                    enableUserInteraction();
                else
                    hungerSpotQuery.addChildEventListener(childEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        if(childEventListener!=null)
            hungerSpotQuery.removeEventListener(childEventListener);

        mHungerSpots.clear();
        readHungerSpots = 0;
    }

    private void enableUserInteraction()
    {
        progressBar.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }


    /*Since pushId's are unique,they are made HashMap keys.When an admin taps on a location to validate
      as a hungerSpot,this function could be called with the key value so that the status could be
      updated from "pending".
     */


    public void onClickValidate(String key) {

        update(key,"validated");

    }

    public void onClickInvalidate(String key){

        update(key,"invalid");
    }

    private void update(String key, final String status){

        final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("HungerSpots").
                child(key);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                HashMap<String,Object> post =(HashMap<String,Object>) dataSnapshot.getValue();
                if(status.equals("validated"))
                    post.put("status","validated");
                else
                    post.put("status","invalid");
                mDatabaseReference.updateChildren(post);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}