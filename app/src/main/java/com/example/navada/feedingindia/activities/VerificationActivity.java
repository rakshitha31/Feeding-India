package com.example.navada.feedingindia.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.navada.feedingindia.R;
import com.example.navada.feedingindia.pojos.FeedingIndiaDonor;
import com.example.navada.feedingindia.pojos.HungerHero;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    private TextView timerTextView;
    private EditText verificationCodeEditText;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private String userName,userEmail,userPassword,userMobileNumber,userDoB,userType,verificationId,callingActivity;
    private CountDownTimer mCountDownTimer;
    private SharedPreferences mSharedPreferences;
    private PhoneAuthProvider.ForceResendingToken token;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        timerTextView = findViewById(R.id.timerTextView);
        verificationCodeEditText = findViewById(R.id.verificationCodeEditText);
        mSharedPreferences = this.getSharedPreferences(getPackageName(),MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        userEmail = intent.getStringExtra("userEmail");
        userPassword = intent.getStringExtra("userPassword");
        userMobileNumber = intent.getStringExtra("userMobileNumber");
        userDoB = intent.getStringExtra("userDoB");
        userType = intent.getStringExtra("userType");
        verificationId = intent.getStringExtra("verificationId");
        callingActivity = intent.getStringExtra("callingActivity");
        token = (callingActivity.equals("HungerHeroSignUpActivity")) ? HungerHeroSignUpActivity.token : DonorSignUpActivity.token;
        startTimer();
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
                startTimer();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCountDownTimer!=null)
        mCountDownTimer.cancel();
    }

    private void startTimer(){

        if(mCountDownTimer!=null)
        mCountDownTimer.cancel();
        mCountDownTimer = new CountDownTimer(61100, 1000) {
            @Override
            public void onTick(long l) {
                String mTimeToDisplay = (l / 1000 - 1) + ":00";
                timerTextView.setText(mTimeToDisplay);
            }

            @Override
            public void onFinish() {

                makeToast("Time out! Try again");
                finish();

            }
        }.start();

    }

    public void onClickResendCode(View view){

        mProgressDialog.setMessage("Resending Code");
        mProgressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                userMobileNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }

    public void onClickVerifyButton(View view){

        String codeEntered = verificationCodeEditText.getText().toString().trim();

        if(codeEntered.isEmpty())
            makeToast("Please enter the verification code");
        else {

            mProgressDialog.setMessage("Verifying Credentials");
            mProgressDialog.show();

            final PhoneAuthCredential mPhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId,codeEntered);

            mAuth.signInWithCredential(mPhoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {

                        mCountDownTimer.cancel();

                        /*Since we want users to sign up with their email,auth object constructed using
                          the phone number is deleted */

                        mAuth.getCurrentUser().delete();
                        signUpWithEmail();

                    }
                    else {
                        mProgressDialog.cancel();
                        verificationCodeEditText.setText("");
                        makeToast(task.getException().getMessage());
                        }
                }
            });
        }

    }

    private void signUpWithEmail()
    {
        mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    if(callingActivity.equals("HungerHeroSignUpActivity")){
                        //User wants to be a hungerhero
                        HungerHero hungerHero = new HungerHero(userName,userDoB,userEmail,userPassword,userMobileNumber,
                         "hungerhero",false,HungerHeroSignUpActivity.educationalBackground,HungerHeroSignUpActivity.state,
                          HungerHeroSignUpActivity.city,HungerHeroSignUpActivity.locality,HungerHeroSignUpActivity.pinCode,
                          HungerHeroSignUpActivity.reasonForJoining,HungerHeroSignUpActivity.affordableTime,
                          HungerHeroSignUpActivity.responsibility,HungerHeroSignUpActivity.currentlyPartOf,
                          HungerHeroSignUpActivity.introducedToFIThrough,HungerHeroSignUpActivity.aboutMeList);

                        mDatabaseReference.child(mAuth.getCurrentUser().getUid()).setValue(hungerHero);
                    }
                    else {

                        //User wants to be a donor
                        FeedingIndiaDonor feedingIndiaDonor = new FeedingIndiaDonor(userName, userEmail, userPassword, userMobileNumber, userDoB, "normal",false);
                        mDatabaseReference.child(mAuth.getCurrentUser().getUid()).setValue(feedingIndiaDonor);

                    }

                    sendVerificationMail();

                }
                else {

                    mProgressDialog.cancel();
                    makeToast(task.getException().getMessage());
                    finish();
                }
            }
        });
    }

    private void sendVerificationMail()
    {

        FirebaseUser mFireBaseUser = mAuth.getCurrentUser();
        mFireBaseUser.sendEmailVerification();
        mAuth.signOut();
        mSharedPreferences.edit().putString("name", userName).apply();
        mSharedPreferences.edit().putString("email", userEmail).apply();
        mSharedPreferences.edit().putString("password", userPassword).apply();
        mSharedPreferences.edit().putString("doB",userDoB).apply();
        mSharedPreferences.edit().putString("mobileNumber", userMobileNumber).apply();
        mSharedPreferences.edit().putString("userType",userType).apply();
        mProgressDialog.cancel();
        makeToast("Welcome to Feeding India!\n" +
                  "A verification mail has been sent to you\n" +
                  "Please verify your email before you sign in");
        finish();

    }

    public void makeToast(String message){
        Toast.makeText(VerificationActivity.this,message,Toast.LENGTH_SHORT).show();
    }
}
