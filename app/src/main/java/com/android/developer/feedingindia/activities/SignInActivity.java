package com.android.developer.feedingindia.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.developer.feedingindia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView forgotPasswordTextView;
    private CheckBox rememberMeCheckBox;
    private ProgressDialog mProgressDialog;
    private Intent intent;
    private SharedPreferences mSharedPreferences;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFireBaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        mAuth = FirebaseAuth.getInstance();
        mSharedPreferences = this.getSharedPreferences(getPackageName(), MODE_PRIVATE);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        else
            mBuilder = new AlertDialog.Builder(this);

        mAlertDialog = mBuilder.setTitle("Send Verification Mail")
                .setMessage("Not received the verification mail yet?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(mAuth.getCurrentUser()!=null)
                            mAuth.getCurrentUser().sendEmailVerification();
                        makeToast("Mail sent!");

                    }
                })

                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {  } })

                .setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {

                        if(mAuth.getCurrentUser()!=null)
                            mAuth.signOut();
                    }
                }).create();


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            /*Triggered when attached to a FireBase Auth object
            and every time the auth state changes i.e,when the user
            signs in and signs out.
            */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFireBaseUser = firebaseAuth.getCurrentUser();
                if(mFireBaseUser!=null) {
                    //Signed In
                    if (mFireBaseUser.isEmailVerified()) {
                        //Check if the email is verified
                        intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth.addAuthStateListener(mAuthStateListener);

        rememberMeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if(checked)
                    mSharedPreferences.edit().putBoolean("remember", true).apply();
                else
                    mSharedPreferences.edit().putBoolean("remember", false).apply();
            }
        });

        if(mSharedPreferences.getBoolean("remember",false))
        {
            emailEditText.setText(mSharedPreferences.getString("email",""));
            passwordEditText.setText(mSharedPreferences.getString("password",""));
            rememberMeCheckBox.setChecked(true);
        }

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(SignInActivity.this,ResetPasswordActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        //Detaching listener when the activity is no longer visible

        if(mAuthStateListener!=null)
            mAuth.removeAuthStateListener(mAuthStateListener);

        if(mAuth.getCurrentUser()!=null)
            if(!mAuth.getCurrentUser().isEmailVerified())
                mAuth.signOut();

        if(mAlertDialog!=null)
            mAlertDialog.dismiss();

    }

    public void onClickSignInButton(View view){

        final String userEmail,userPassword;

        userEmail = emailEditText.getText().toString().trim();
        userPassword = passwordEditText.getText().toString();

        if(userEmail.isEmpty() || userPassword.isEmpty())
            makeToast("Fields cannot be empty!");
        else {

            mProgressDialog.setMessage("Signing in...");
            mProgressDialog.show();
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Log.i("Hello", "onComplete: ");

                    mProgressDialog.cancel();

                    if (task.isSuccessful()) {
                        //Signed in
                        if (mAuth.getCurrentUser().isEmailVerified()){

                            String storedEmail = mSharedPreferences.getString("email", "");

                            if (storedEmail.equals("") || !(userEmail.equals(storedEmail)))
                                mSharedPreferences.edit().putBoolean("fetch", true).apply();

                            else if (rememberMeCheckBox.isChecked())
                                mSharedPreferences.edit().putString("password", userPassword).apply();
                        }

                        else {
                            makeToast("Email not verified yet!");
                            mAlertDialog.show();
                        }
                    }
                    else
                        makeToast(task.getException().getMessage());
                }
            });
        }

    }

    public void onClickSignUpButton(View view){

        intent = new Intent(SignInActivity.this,DonorSignUpActivity.class);
        startActivity(intent);

    }

    public void onClickHungerHeroSignUpButton(View view){

        intent = new Intent(SignInActivity.this,HungerHeroSignUpActivity.class);
        startActivity(intent);

    }

    public void makeToast(String message){
        Toast.makeText(SignInActivity.this,message,Toast.LENGTH_SHORT).show();
    }

}