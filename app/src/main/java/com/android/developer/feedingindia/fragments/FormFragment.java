package com.android.developer.feedingindia.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.developer.feedingindia.R;
import com.android.developer.feedingindia.pojos.HungerHero;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;

public class FormFragment extends Fragment {

    private EditText educationalBackgroundEditText,currentlyPartOfEditText,cityEditText,localityEditText,
                     pinCodeEditText,reasonForJoiningEditText,aboutMeEditText;
    private String responsibility = "hungerhero";
    private String affordableTime = "3-6 hours";
    private String state;
    private ArrayList<String> introducedToFIThrough;
    private Spinner spinner;
    private ArrayAdapter spinnerAdapter;
    private Button hungerHeroDetailsSubmitButton;
    private CheckBox checkBox1,checkBox2,checkBox3,checkBox4;
    private RadioButton radioButton1,radioButton2,radioButton3,radioButton4,radioButton5,radioButton6,radioButton7;
    private ProgressDialog mProgressDialog;
    private SharedPreferences mSharedPreferences;
    private DatabaseReference mDatabaseReference;

    public FormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        introducedToFIThrough = new ArrayList<>();
        mSharedPreferences = getActivity().getSharedPreferences("com.example.navada.feedingindia", Context.MODE_PRIVATE);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");

        spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.india_states, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        responsibility = "hungerhero";
        affordableTime = "3-6 hours";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_form, container, false);
        educationalBackgroundEditText = view.findViewById(R.id.educationalBackgroundEditText);
        currentlyPartOfEditText = view.findViewById(R.id.currentlyPartOfEditText);
        cityEditText = view.findViewById(R.id.cityEditText);
        localityEditText = view.findViewById(R.id.localityEditText);
        pinCodeEditText = view.findViewById(R.id.pinCodeEditText);
        reasonForJoiningEditText = view.findViewById(R.id.reasonForJoiningEditText);
        aboutMeEditText = view.findViewById(R.id.aboutMeEditText);
        spinner = view.findViewById(R.id.spinner);
        spinner.setAdapter(spinnerAdapter);
        hungerHeroDetailsSubmitButton = view.findViewById(R.id.hungerHeroDetailsSubmitButton);
        checkBox1 = view.findViewById(R.id.checkBox1);
        checkBox2 = view.findViewById(R.id.checkBox2);
        checkBox3 = view.findViewById(R.id.checkBox3);
        checkBox4 = view.findViewById(R.id.checkBox4);
        radioButton1 = view.findViewById(R.id.radioButton1);
        radioButton2 = view.findViewById(R.id.radioButton2);
        radioButton3 = view.findViewById(R.id.radioButton3);
        radioButton4 = view.findViewById(R.id.radioButton4);
        radioButton5 = view.findViewById(R.id.radioButton5);
        radioButton6 = view.findViewById(R.id.radioButton6);
        radioButton7 = view.findViewById(R.id.radioButton7);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        radioButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                affordableTime = "3-6 hours";
            }
        });

        radioButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                affordableTime = "6-9 hours";
            }
        });

        radioButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                affordableTime = "9-12 hours";
            }
        });

        radioButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               responsibility = "hungerhero";
            }
        });

        radioButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responsibility = "superhero";
            }
        });

        radioButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                affordableTime = "12-15 hours";
            }
        });

        radioButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                affordableTime = "15+ hours";
            }
        });

        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    if(!introducedToFIThrough.contains("Facebook"))
                    introducedToFIThrough.add("Facebook");
                }
                else
                    if(introducedToFIThrough.contains("Facebook"))
                        introducedToFIThrough.remove("Facebook");

            }
        });

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    if(!introducedToFIThrough.contains("Website"))
                    introducedToFIThrough.add("Website");
                }
                else
                if(introducedToFIThrough.contains("Website"))
                    introducedToFIThrough.remove("Website");

            }
        });

        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    if(!introducedToFIThrough.contains("Media/News"))
                    introducedToFIThrough.add("Media/News");
                }
                else
                if(introducedToFIThrough.contains("Media/News"))
                    introducedToFIThrough.remove("Media/News");

            }
        });

        checkBox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    if(!introducedToFIThrough.contains("Through a Friend"))
                    introducedToFIThrough.add("Through a Friend");
                }
                else
                if(introducedToFIThrough.contains("Through a Friend"))
                    introducedToFIThrough.remove("Through a Friend");

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

        hungerHeroDetailsSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSubmitButton();
            }
        });

    }

    private void onClickSubmitButton(){

        String educationalBackground = educationalBackgroundEditText.getText().toString().trim();
        String currentlyPartOf = currentlyPartOfEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String locality = localityEditText.getText().toString().trim();
        String pinCode = pinCodeEditText.getText().toString().trim();
        String reasonForJoining = reasonForJoiningEditText.getText().toString().trim();
        String aboutMe[] = aboutMeEditText.getText().toString().trim().split(",");
        ArrayList<String> aboutMeList = new ArrayList<>();

        if(educationalBackground.isEmpty() || currentlyPartOf.isEmpty() || city.isEmpty() ||
           locality.isEmpty() || pinCode.isEmpty() || reasonForJoining.isEmpty() || !(aboutMe.length>0)
                || !(introducedToFIThrough.size()>0))
            makeToast("Fields marked with * cannot be empty!");

        else {
            mProgressDialog.show();
            Collections.addAll(aboutMeList,aboutMe);
            String name = mSharedPreferences.getString("name","");
            String dateOfBirth = mSharedPreferences.getString("doB","");
            String email = mSharedPreferences.getString("email","");
            String password = mSharedPreferences.getString("password","");
            String mobileNumber = mSharedPreferences.getString("mobileNumber","");
            mSharedPreferences.edit().putString("userType","hungerhero").apply();
            mSharedPreferences.edit().putBoolean("clear",true).apply();
            HungerHero hungerHero = new HungerHero(name,dateOfBirth,email,password,mobileNumber,"hungerhero",false,
                    educationalBackground,state,city,locality,pinCode,reasonForJoining,affordableTime,responsibility,
                    currentlyPartOf,introducedToFIThrough,aboutMeList);
            mDatabaseReference.setValue(hungerHero);
            mProgressDialog.cancel();
            makeToast("Congo! You are a "+responsibility+" now");
        }

    }

    private void makeToast(String message){

        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

}
