package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {


    private TextView username;
    private TextView fullName;
    private TextView car;
    private ImageView profilePic;
    final static int Gallery_Pick=1;
    FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference userProfileImageReference;
    private Spinner dropdown;
    private ArrayList<String> carList;
    private ArrayList<String> makeList;
    private Spinner modelSpinner;

    ProgressDialog loadingbar;

    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        username=findViewById(R.id.profile_username);
        fullName=findViewById(R.id.fullname);
        car=findViewById(R.id.profile_carname);
        loadingbar= new ProgressDialog(this);
        profilePic=findViewById(R.id.displaypic);
        mAuth=FirebaseAuth.getInstance();
        userProfileImageReference=FirebaseStorage.getInstance().getReference().child("Profile Images");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        currentUserId=mAuth.getCurrentUser().getUid();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        addMake();


    }


    public void saveUser(View view) {
if(username.getText().toString().isEmpty()||fullName.getText().toString().isEmpty()||car.getText().toString().isEmpty())
{
    Toast.makeText(getApplicationContext(),"Please make sure all fields are filled",Toast.LENGTH_LONG).show();
}
else {
    Map<String, Object> userMap = new HashMap<>();
    userMap.put("username", username.getText().toString());
    userMap.put("fullname", fullName.getText().toString());
    userMap.put("car", car.getText().toString());
    userMap.put("dob", "Date here");
    userMap.put("status", "status here");
    userMap.put("country", "Canada");


    usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
                sendToNews();
            } else {
                String message = task.getException().getMessage();
                Toast.makeText(getApplicationContext(), "ERROR:" + message, Toast.LENGTH_LONG).show();
                loadingbar.dismiss();
            }
        }


    });
}
    }
    private void sendToNews() {

       /* CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                // Implement your code here
            }
        });
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        */

        Intent setup = new Intent(this,NewsActivity.class);
        setup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setup);
        finish();
    }

    public void onPic (View view){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingbar.setTitle("Profile Image");
                loadingbar.setMessage("Please wait, while we updating your profile image...");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImageReference.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
                            StorageReference filePath = userProfileImageReference.child(currentUserId + ".jpg");
                            final String downloadUrl = filePath.getDownloadUrl().toString();
                            usersRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetupActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
            }
        }
    }


    public void addMake(){

     dropdown = findViewById(R.id.make);
   makeList = new ArrayList<>();
         carList= new ArrayList<>();
      modelSpinner= findViewById(R.id.make);



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

}


