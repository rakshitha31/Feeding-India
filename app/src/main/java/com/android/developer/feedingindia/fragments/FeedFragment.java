package com.android.developer.feedingindia.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.pojos.DonationDetails;
import com.android.developer.feedingindia.pojos.HungerSpot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;
import static com.android.developer.feedingindia.fragments.DonateFragment.longitude;
import static com.android.developer.feedingindia.fragments.DonateFragment.latitude;

public class FeedFragment extends Fragment implements OnMapReadyCallback{

    private DatabaseReference hungerSpotDatabaseReference, donorSpotDatabaseReference;
    private ChildEventListener hungerSpotChildEventListener, donorSpotChildEventListener;
    private ProgressBar progressBar;
    private LinearLayout mLinearLayout;
    private ArrayList<HungerSpot> hungerSpots;
    private ArrayList<DonationDetails> donations;
    private long donationCount = 0, readDonationCount = 0;
    private long hungerSpotCount = 0, readHungerSpotCount = 0;
    private boolean doneReadingDonations = false;
    private boolean doneReadingHungerSpots = false;

    //Google map variables
    private GoogleMap mMap;
    private Marker feedMarker,marker;
    private final static int MY_PERMISSION_COARSE_LOCATION = 101;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleApiClient mGoogleApiClient;
    LatLng latlng;
    MapView mapView;
    MapFragment mapFragment;


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
                if (!hungerSpots.contains(hungerSpot))
                    hungerSpots.add(hungerSpot);
                readHungerSpotCount++;
                if (readHungerSpotCount == hungerSpotCount)
                    doneReadingHungerSpots = true;

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

        donorSpotChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ObjectMapper myMapper = new ObjectMapper();
                HashMap<String, HashMap<String, DonationDetails>> myList = (HashMap<String, HashMap<String, DonationDetails>>) dataSnapshot.getValue();
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
        SupportMapFragment supportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.feedmap);
        supportMapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        //to-do else part

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

                if (donationCount != 0)
                    donorSpotDatabaseReference.addChildEventListener(donorSpotChildEventListener);
                else
                    doneReadingDonations = true;

                if (doneReadingHungerSpots && doneReadingDonations)
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

                if (hungerSpotCount != 0)
                    hungerSpotDatabaseReference.addChildEventListener(hungerSpotChildEventListener);
                else
                    doneReadingHungerSpots = true;

                if (doneReadingHungerSpots && doneReadingDonations)
                    enableUserInteraction();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        hungerSpotDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                for(DataSnapshot s:dataSnapshot.getChildren()){

                    LatLng newFeedLocation = new LatLng(
                            dataSnapshot.child("latitude").getValue(Long.class),
                            dataSnapshot.child("longitude").getValue(Long.class)
                    );
                    mMap.addMarker(new MarkerOptions()
                            .position(newFeedLocation)
                            .title(dataSnapshot.getKey()));
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                    

                }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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
