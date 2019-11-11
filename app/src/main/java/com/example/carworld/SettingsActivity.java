package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private EditText username;
    private EditText userprofilename,userstatus,userlocation;
    private Button UpdateAccountSettingsButton;

    private ImageView userProfileImage;
    private Spinner dropdown;
    private ArrayList<String> carList;
    private ArrayList<String> makeList;

    private DatabaseReference Settingsuserref;
    private FirebaseAuth mAuth;


    private String currentUserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dropdown = (Spinner)findViewById(R.id.settings_car);
        username= (EditText) findViewById(R.id.settings_username);
        userprofilename= (EditText) findViewById(R.id.settings_fullname);
        userstatus=(EditText)findViewById(R.id.settings_status);
        userlocation=(EditText)findViewById(R.id.settings_userlocation);
        userProfileImage=(CircleImageView) findViewById(R.id.settings_profile_image);

        addCars();
        mAuth=FirebaseAuth.getInstance();
        currentUserid=mAuth.getCurrentUser().getUid();

        Settingsuserref= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        UpdateAccountSettingsButton=(Button)findViewById(R.id.update_account_settings_button);


Settingsuserref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
       if (dataSnapshot.exists()){

           String myUserName = dataSnapshot.child("username").getValue().toString();
           String myProfileName = dataSnapshot.child("fullname").getValue().toString();
           String myProfileCar = dataSnapshot.child("car").getValue().toString();
           String myProfileStatus = dataSnapshot.child("status").getValue().toString();
           String myProfilelocation= dataSnapshot.child("location").getValue().toString();
           String myProfiledob = dataSnapshot.child("dob").getValue().toString();

           if(dataSnapshot.child("profileimage").exists()) {
               String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();

               Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
           }

           username.setText(myUserName);
           userprofilename.setText(myProfileName);
           userstatus.setText(myProfileStatus);
           userlocation.setText(myProfilelocation);

           //setting value for car dropdown
           ArrayAdapter carAdap= (ArrayAdapter)dropdown.getAdapter();
           dropdown.setSelection(carAdap.getPosition(myProfileCar));

          dropdown.setSelection(getIndex(dropdown,myProfileCar));
       }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});


UpdateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        ValidateAccountInfo();

    }
});

    }

    private void ValidateAccountInfo() {
        String name= username.getText().toString();
        String profilename=userprofilename.getText().toString();
        String carname= dropdown.getSelectedItem().toString();
        String status = userstatus.getText().toString();
        String location =userlocation.getText().toString();


        if(TextUtils.isEmpty(name))
            {
                Toast.makeText(this,"Please write username",Toast.LENGTH_SHORT);
            }
          else  if(TextUtils.isEmpty(profilename))
                {
                    Toast.makeText(this,"Please write your name",Toast.LENGTH_SHORT);
                }

              else  if(TextUtils.isEmpty(carname))
                    {
                        Toast.makeText(this,"Please select a car",Toast.LENGTH_SHORT);
                    }

                    else if (TextUtils.isEmpty(name))
                        {
                            Toast.makeText(this,"Please enter status",Toast.LENGTH_SHORT);
                        }
        else if (TextUtils.isEmpty(location))
        {
            Toast.makeText(this,"Please enter location",Toast.LENGTH_SHORT);
        }
                    else {

                        UpdateAccountInfo(name,profilename,carname,status,location);

        }



        /*
        String dob= username.toString();
        String county= username.toString();

         */




    }

    private void UpdateAccountInfo(String name, String profilename, String carname, String status,String location) {

        HashMap userMap= new HashMap<>();

        userMap.put("username", name);
        userMap.put("fullname", profilename);
        userMap.put("car", carname);
       // userMap.put("dob", "Date here");
        userMap.put("status", status);
       userMap.put("location", location);


        Settingsuserref.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Profile Settings Updated", Toast.LENGTH_SHORT).show();


                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), "ERROR:" + message, Toast.LENGTH_LONG).show();

                }
            }


        });
    }


    public void addCars(){


        makeList = new ArrayList<>();
        carList= new ArrayList<>();




        try {




            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("carList.txt")));

            String line = reader.readLine();
            while (line != null) {

                String full[]= line.split(",");

                String year= full[0].substring(full[0].lastIndexOf("(")+1);
                String make=full[1].substring(2,full[1].length()-1);
                String model=full[2].substring(2,full[2].length()-2);

                makeList.add(make + " " + model);
                System.out.println(model);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //

/*
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Toast.makeText(getApplicationContext(),parentView.getItemAtPosition(position).toString(),Toast.LENGTH_LONG).show();

                try {


                    BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("carList.txt")));

                    String line = reader.readLine();
                    String full[] = new String[100000];
                    while (line != null) {
                        if (line.contains(parentView.getItemAtPosition(position).toString())) {
                            full = line.split(",");


                            String model = full[2].substring(2, full[2].length() - 2);
                            modelList.add(model);
                            System.out.println(model);
                            // read next line
                        }
                        line = reader.readLine();

                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }


        });

*/





        carList= returnList(makeList);
        Collections.sort(carList);
        ArrayAdapter<String> makeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, carList);
        dropdown.setAdapter(makeAdapter);
    }


    public ArrayList<String> returnList(ArrayList<String> list)
    {
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(list);
        list.clear();
        list.addAll(hashSet);

        return list;
    }


    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    private void sendToNews() {


        Intent setup = new Intent(this,NewsActivity.class);
        setup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setup);
        finish();
    }
}
