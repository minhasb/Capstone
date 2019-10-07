package com.example.carworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView username;
   private TextView userfullname;
   private TextView usercarname;
   private EditText userbio;
    FirebaseAuth mAuth;
    ProgressDialog loadingbar;
    private DatabaseReference userref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.profile_username);
        userfullname=findViewById(R.id.profile_fullname);
        usercarname=findViewById(R.id.profile_carname);
        userbio=findViewById(R.id.profile_bio);

        userref= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();

        setUserInfo();



    }


    private void setUserInfo(){

        final String current_user_id = mAuth.getCurrentUser().getUid();

        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
              username.setText(dataSnapshot.child(current_user_id).child("username").getValue().toString());
                userfullname.setText(dataSnapshot.child(current_user_id).child("fullname").getValue().toString());
                usercarname.setText(dataSnapshot.child(current_user_id).child("car").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void logout(View view) {

        Intent intent = new Intent (this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
