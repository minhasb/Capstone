package com.example.carworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    private TextView username;
    private TextView userfullname;
    private TextView usercarname;
    private TextView userstatus,userlocation;

    private CircleImageView userProfileImage;

    FirebaseAuth mAuth;
    ProgressDialog loadingbar;
    private DatabaseReference userref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        username = findViewById(R.id.my_username);
        userfullname=findViewById(R.id.my_profile_full_name);
        usercarname=findViewById(R.id.my_car);
        userstatus=findViewById(R.id.my_profile_status);
        userlocation=findViewById(R.id.my_location);
        userProfileImage=(CircleImageView)findViewById(R.id.my_profile_pic);
        userref= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        //to hide keyboard when starting up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
                userstatus.setText(dataSnapshot.child(current_user_id).child("status").getValue().toString());
                usercarname.setText(dataSnapshot.child(current_user_id).child("car").getValue().toString());
                userlocation.setText(dataSnapshot.child(current_user_id).child("location").getValue().toString());

                Picasso.get().load(dataSnapshot.child(current_user_id).child("profileimage").getValue().toString()).into(userProfileImage);
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


