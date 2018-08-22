package com.android.developer.feedingindia.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.developer.feedingindia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class MakeAdminActivity extends AppCompatActivity {

    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_admin);
        emailEditText = findViewById(R.id.emailEditText);

    }

    public void onClickMakeAdmin(View view)
    {

        String email = emailEditText.getText().toString().trim();

        if(email.isEmpty())
            makeToast("Please enter the email");
        else if(email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
            makeToast("You are already an admin!");
        else{
            Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(email);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getChildrenCount()==0)
                        makeToast("Email Not found");
                    else
                    {
                        HashMap<String,Object> userToBeAdmin = (HashMap<String,Object>)dataSnapshot.getValue();
                        String userUID = dataSnapshot.getKey();
                        if(userToBeAdmin.get("userType").equals("admin"))
                            makeToast("The person is already an admin");
                        else {
                            userToBeAdmin.put("requestedToBeAdmin", true);
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                            databaseReference.setValue(userToBeAdmin);
                            makeToast("Success!");
                        }
                        emailEditText.setText("");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void makeToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
