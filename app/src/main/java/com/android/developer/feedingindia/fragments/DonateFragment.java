package com.android.developer.feedingindia.fragments;

import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.pojos.DonationDetails;
import java.util.HashMap;

public class DonateFragment extends Fragment  {

    private EditText foodDescriptionEditText,foodPreparedOnEditText,additionalContactNumberEditText,cityEditText,
                     localityEditText,pinCodeEditText;
    private boolean hasContainer;
    public static double latitude,longitude;
    public static boolean locationChosenOnMap;
    private String state,city,locality,pinCode,foodDescription,foodPreparedOn,additionalContactNumber;
    private SharedPreferences mSharedPreferences;
    private HashMap<String,String> address;
    private Spinner spinner;
    private ArrayAdapter spinnerAdapter;
    private RadioButton hasContainerYesRadioButton,hasContainerNoRadioButton;
    private Button submitButton;
    private ImageButton locationButton;
    private DatabaseReference mDatabaseReference;
    private Context mContext;

    public DonateFragment(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Donations").child(FirebaseAuth.getInstance().getUid());

        spinnerAdapter = ArrayAdapter.createFromResource(mContext, R.array.india_states, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

        mSharedPreferences = getActivity().getSharedPreferences("com.example.navada.feedingindia",Context.MODE_PRIVATE);

        state = locality = pinCode = city = "";
        hasContainer = locationChosenOnMap = false;
        address = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donate, container, false);
        foodDescriptionEditText = view.findViewById(R.id.foodDescriptionEditText);
        foodPreparedOnEditText = view.findViewById(R.id.foodPreparedOnEditText);
        cityEditText = view.findViewById(R.id.cityEditText);
        localityEditText = view.findViewById(R.id.localityEditText);
        pinCodeEditText = view.findViewById(R.id.pinCodeEditText);
        spinner = view.findViewById(R.id.spinner);
        locationButton = view.findViewById(R.id.locationButton);
        additionalContactNumberEditText = view.findViewById(R.id.additionalContactNumberEditText);
        hasContainerYesRadioButton = view.findViewById(R.id.hasContainerYesRadioButton);
        hasContainerNoRadioButton = view.findViewById(R.id.hasContainerNoRadioButton);
        submitButton = view.findViewById(R.id.submitButton);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spinner.setAdapter(spinnerAdapter);

    }

    @Override
    public void onResume() {

        super.onResume();
        hasContainerYesRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    hasContainer = true;
            }
        });


        hasContainerNoRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked)
                    hasContainer = false;
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                state = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubmitButton();
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickLocationButton();
            }
        });

    }

    private void onClickSubmitButton() {

        foodDescription = foodDescriptionEditText.getText().toString().trim();
        foodPreparedOn = foodPreparedOnEditText.getText().toString().trim();
        additionalContactNumber = additionalContactNumberEditText.getText().toString().trim();

        /*Values of variables locationChosenOnMap,latitude,longitude are to be
        changed if location is retrieved on the map
         */

        if (foodDescription.isEmpty() || foodPreparedOn.isEmpty())
            makeToast("Fields marked with * cannot be empty!");
        else if (!locationChosenOnMap) {
            city = cityEditText.getText().toString().trim();
            locality = localityEditText.getText().toString().trim();
            pinCode = pinCodeEditText.getText().toString().trim();
        }
            if (pinCode.isEmpty() || city.isEmpty() || locality.isEmpty())
                makeToast("Enter full address or locate on the map");
            else {
                if (additionalContactNumber.isEmpty())
                    additionalContactNumber = null;

                address.put("city", city);
                address.put("state", state);
                address.put("locality", locality);
                address.put("pinCode", pinCode);

                if (locationChosenOnMap) {
                    address.put("latitude", Double.toString(latitude));
                    address.put("longitude", Double.toString(longitude));
                }

                askIfUserCanDonate();

            }
    }

    private void askIfUserCanDonate(){

        AlertDialog.Builder mBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            mBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        }

        mBuilder.setMessage("Can you donate it yourself?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        DonationDetails donationDetails = new DonationDetails(foodDescription,foodPreparedOn,
                                                            additionalContactNumber,"done",mSharedPreferences.getString("mobileNumber",""),
                                                            hasContainer,true,address);
                        mDatabaseReference.push().setValue(donationDetails);
                        reset();
                        //Show Hunger Spots

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        DonationDetails donationDetails = new DonationDetails(foodDescription,foodPreparedOn,
                                additionalContactNumber,"pending",mSharedPreferences.getString("mobileNumber",""),
                                hasContainer,false,address);
                        mDatabaseReference.push().setValue(donationDetails);
                        reset();

                    }
                }).show();

    }

    private void reset(){
        state = locality = pinCode = city = "";
        locationChosenOnMap = false;
        address.clear();
        foodDescriptionEditText.setText("");
        foodPreparedOnEditText.setText("");
        cityEditText.setText("");
        localityEditText.setText("");
        pinCodeEditText.setText("");
        additionalContactNumberEditText.setText("");
        hasContainerNoRadioButton.setChecked(true);
    }

    private void onClickLocationButton(){

        //Map view for selecting user location
    }

    private void makeToast(String message){
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

}
