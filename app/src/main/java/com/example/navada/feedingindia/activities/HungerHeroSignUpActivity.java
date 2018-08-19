package com.example.navada.feedingindia.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.navada.feedingindia.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class HungerHeroSignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameEditText,emailEditText,passwordEditText,mobileNumberEditText,educationalBackgroundEditText,
            currentlyPartOfEditText,cityEditText,localityEditText,pinCodeEditText,reasonForJoiningEditText,aboutMeEditText;
    private Button datePickerButton;
    private String userName,userEmail,userPassword,userMobileNumber,userDoB;
    public static String educationalBackground,currentlyPartOf,city,locality,pinCode,reasonForJoining;
    public static String responsibility = "hungerhero";
    public static String affordableTime = "3-6 hours";
    public static String state;
    public static ArrayList<String> aboutMeList,introducedToFIThrough;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private int mDay,mMonth,mYear;
    private ProgressDialog mProgressDialog;
    public static PhoneAuthProvider.ForceResendingToken token;
    private Spinner spinner;
    private CheckBox checkBox1,checkBox2,checkBox3,checkBox4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunger_hero_sign_up);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        educationalBackgroundEditText = findViewById(R.id.educationalBackgroundEditText);
        currentlyPartOfEditText = findViewById(R.id.currentlyPartOfEditText);
        cityEditText = findViewById(R.id.cityEditText);
        localityEditText = findViewById(R.id.localityEditText);
        pinCodeEditText = findViewById(R.id.pinCodeEditText);
        reasonForJoiningEditText = findViewById(R.id.reasonForJoiningEditText);
        aboutMeEditText = findViewById(R.id.aboutMeEditText);

        responsibility = "hungerhero";
        affordableTime = "3-6 hours";
        userDoB = "empty";
        introducedToFIThrough = new ArrayList<>();
        datePickerButton = findViewById(R.id.datePickerButton);

        Calendar mCalender = Calendar.getInstance();
        mYear = mCalender.get(Calendar.YEAR);
        mMonth = mCalender.get(Calendar.MONTH);
        mDay = mCalender.get(Calendar.DAY_OF_MONTH);

        spinner = findViewById(R.id.spinner);

        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);

        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.india_states, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

    }

    @Override
    protected void onResume() {
        super.onResume();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mProgressDialog.cancel();
                makeToast(e.getMessage());
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mProgressDialog.cancel();

                token = forceResendingToken;
                Intent intent = new Intent(HungerHeroSignUpActivity.this,VerificationActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userMobileNumber",userMobileNumber);
                intent.putExtra("userDoB",userDoB);
                intent.putExtra("userType","hungerhero");
                intent.putExtra("callingActivity","HungerHeroSignUpActivity");
                intent.putExtra("verificationId",verificationId);
                startActivity(intent);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

        };

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

    }

    public void onClickDatePickerButton(View view){

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(HungerHeroSignUpActivity.this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

                userDoB = dayOfMonth+"/"+(month+1)+"/"+year;
                mDay = dayOfMonth;
                mMonth = month;
                mYear = year;
                datePickerButton.setText(userDoB);

            }
        },mYear,mMonth,mDay);

        mDatePickerDialog.show();
    }

    public void onClickSubmitButton(View view){


        userName = nameEditText.getText().toString().trim();
        userEmail = emailEditText.getText().toString().trim();
        userPassword = passwordEditText.getText().toString();
        userMobileNumber = mobileNumberEditText.getText().toString().trim();
        educationalBackground = educationalBackgroundEditText.getText().toString().trim();
        currentlyPartOf = currentlyPartOfEditText.getText().toString().trim();
        city = cityEditText.getText().toString().trim();
        locality = localityEditText.getText().toString().trim();
        pinCode = pinCodeEditText.getText().toString().trim();
        reasonForJoining = reasonForJoiningEditText.getText().toString().trim();
        String aboutMe[] = aboutMeEditText.getText().toString().trim().split(",");
        ArrayList<String> aboutMeList = new ArrayList<>();

        if(userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || userMobileNumber.isEmpty() ||
           userDoB.equals("empty") || educationalBackground.isEmpty() || currentlyPartOf.isEmpty() || city.isEmpty() ||
           locality.isEmpty() || pinCode.isEmpty() || reasonForJoining.isEmpty() || !(aboutMe.length>0) || !(introducedToFIThrough.size()>0))
            makeToast("Fields cannot be empty!");

        else if(userPassword.length()<6)
            makeToast("Password should be minimum of 6 characters!");

        else{

            Collections.addAll(aboutMeList,aboutMe);
            //Verify Phone Number
            PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber mPhoneNumber = mPhoneNumberUtil.parse(userMobileNumber,"IN");
                if(mPhoneNumberUtil.isValidNumber(mPhoneNumber))
                {
                    mProgressDialog.setMessage("Sending Verification Code...");
                    userMobileNumber = mPhoneNumberUtil.format(mPhoneNumber,PhoneNumberUtil.PhoneNumberFormat.E164);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            userMobileNumber,
                            60,
                            TimeUnit.SECONDS,
                            this,
                            mCallbacks);
                    mProgressDialog.show();
                }
                else
                    makeToast("Please enter a valid mobile number!");
            } catch (NumberParseException e) {
                makeToast(e.getMessage());
            }
        }

    }

    public void makeToast(String message){
        Toast.makeText(HungerHeroSignUpActivity.this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.radioButton1 :
                affordableTime = "3-6 hours";
                break;

            case R.id.radioButton2 :
                affordableTime = "6-9 hours";
                break;

            case R.id.radioButton3 :
                affordableTime = "9-12 hours";
                break;

            case R.id.radioButton4 :
                responsibility = "hungerhero";
                break;

            case R.id.radioButton5 :
                responsibility = "superhero";
                break;

            case R.id.radioButton6 :
                affordableTime = "12-15 hours";
                break;

            case R.id.radioButton7 :
                affordableTime = "15+ hours";
                break;
        }

    }
}
