package com.example.navada.feedingindia.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.navada.feedingindia.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DonorSignUpActivity extends AppCompatActivity {

    private EditText nameEditText,emailEditText,passwordEditText,mobileNumberEditText;
    private Button datePickerButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String userName,userEmail,userPassword,userMobileNumber,userDoB;
    private int mDay,mMonth,mYear;
    private ProgressDialog mProgressDialog;
    public static PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_sign_up);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mobileNumberEditText = findViewById(R.id.mobileNumberEditText);
        datePickerButton = findViewById(R.id.datePickerButton);
        userDoB = "empty";
        Calendar mCalender = Calendar.getInstance();
        mYear = mCalender.get(Calendar.YEAR);
        mMonth = mCalender.get(Calendar.MONTH);
        mDay = mCalender.get(Calendar.DAY_OF_MONTH);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Sending Verification Code...");

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
                Intent intent = new Intent(DonorSignUpActivity.this,VerificationActivity.class);
                intent.putExtra("userName",userName);
                intent.putExtra("userEmail",userEmail);
                intent.putExtra("userPassword",userPassword);
                intent.putExtra("userMobileNumber",userMobileNumber);
                intent.putExtra("userDoB",userDoB);
                intent.putExtra("userType","normal");
                intent.putExtra("verificationId",verificationId);
                intent.putExtra("callingActivity","DonorSignUpActivity");
                startActivity(intent);

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }

        };
    }

    public void onClickDatePickerButton(View view){

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(DonorSignUpActivity.this,new DatePickerDialog.OnDateSetListener() {
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

        if(userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || userMobileNumber.isEmpty() || userDoB.equals("empty"))
            makeToast("Fields cannot be empty!");
        else if(userPassword.length()<6)
            makeToast("Password should be minimum of 6 characters!");
        else{
            //Verify Phone Number
            PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber mPhoneNumber = mPhoneNumberUtil.parse(userMobileNumber,"IN");
                if(mPhoneNumberUtil.isValidNumber(mPhoneNumber))
                {
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
        Toast.makeText(DonorSignUpActivity.this,message,Toast.LENGTH_SHORT).show();
    }

}
