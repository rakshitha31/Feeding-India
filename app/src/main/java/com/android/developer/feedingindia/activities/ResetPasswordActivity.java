package com.android.developer.feedingindia.activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.developer.feedingindia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private ProgressDialog mProgressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailEditText = findViewById(R.id.emailEditText);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
    }

    public void onClickResetPassword(View view){

        String email = emailEditText.getText().toString().trim();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(email.isEmpty())

            makeToast("Please enter your email");

        else {

            mProgressDialog.show();
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {

                @Override
                public void onSuccess(Void aVoid) {

                    mProgressDialog.cancel();
                    makeToast("Mail Sent!You can reset your password now");
                    finish();

                }

            }).addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception e) {

                    mProgressDialog.cancel();
                    makeToast(e.getMessage());

                }

            });
        }

    }

    public void makeToast(String message){

        Toast.makeText(ResetPasswordActivity.this,message,Toast.LENGTH_SHORT).show();

    }

}