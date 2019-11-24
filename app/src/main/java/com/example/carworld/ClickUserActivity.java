package com.example.carworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickUserActivity extends AppCompatActivity {

    private TextView username;
    private TextView userfullname;
    private TextView usercarname;
    private TextView userstatus,userlocation;
    private String userid,userImageUrl,groupownerid,PostKey;
    private Button MsgBtn,DeleteUserBtn;

    private CircleImageView userProfileImage;

    FirebaseAuth mAuth;
    ProgressDialog loadingbar;
    private DatabaseReference userref,groupref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_user);

        userid=getIntent().getExtras().get("userid").toString();

        groupownerid=getIntent().getExtras().get("groupownerid").toString();
        PostKey=getIntent().getExtras().get("PostKey").toString();



        username = findViewById(R.id.my_username);
        userfullname=findViewById(R.id.my_profile_full_name);
        usercarname=findViewById(R.id.my_car);
        userstatus=findViewById(R.id.my_profile_status);
        userlocation=findViewById(R.id.my_location);
        userProfileImage=(CircleImageView)findViewById(R.id.my_profile_pic);
        userref= FirebaseDatabase.getInstance().getReference().child("Users");
        groupref=FirebaseDatabase.getInstance().getReference().child("Groups").child(PostKey);

        MsgBtn = findViewById(R.id.message);
        DeleteUserBtn=findViewById(R.id.delete);
        DeleteUserBtn.setVisibility(View.INVISIBLE);


        mAuth=FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser().getUid().equals(userid)) {
            MsgBtn.setVisibility(View.INVISIBLE);
        }

        if(mAuth.getCurrentUser().getUid().equals(groupownerid)) {
            DeleteUserBtn.setVisibility(View.VISIBLE);

            if(mAuth.getCurrentUser().getUid().equals(userid))
            {
                DeleteUserBtn.setVisibility(View.INVISIBLE);

            }
        }
        //to hide keyboard when starting up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setUserInfo();



    }


    private void setUserInfo(){



         username.setText(getIntent().getExtras().get("username").toString());
                userfullname.setText(getIntent().getExtras().get("fullname").toString());
                userstatus.setText(getIntent().getExtras().get("status").toString());
                usercarname.setText(getIntent().getExtras().get("car").toString());
                userlocation.setText(getIntent().getExtras().get("location").toString());

                Picasso.get().load(getIntent().getExtras().get("profileimage").toString()).into(userProfileImage);


    }

    public void logout(View view) {

        Intent intent = new Intent (this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendMessage(View view) {
        Intent intent = new Intent (this,SendMessageActivity.class);
        intent.putExtra("userid",userid);
        intent.putExtra("userimage",getIntent().getExtras().get("profileimage").toString());
        intent.putExtra("username",getIntent().getExtras().get("username").toString());
        startActivity(intent);
        finish();
    }

    public void deleteUser(View view) {


        groupref.child(userid).removeValue();
        groupref.child("Members").child(userid).removeValue();
        Toast.makeText(getApplicationContext(),"Member deleted from group", Toast.LENGTH_LONG).show();

        Intent intent = new Intent (this,GroupMembersActivity.class);
        intent.putExtra("PostKey",PostKey);
        startActivity(intent);
        finish();

    }
}
