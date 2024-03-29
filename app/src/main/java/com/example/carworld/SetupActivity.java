package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {


    private TextView username;
    private TextView fullName;

    private ImageView profilePic;
    final static int Gallery_Pick=1;
    FirebaseAuth mAuth;

    private DatabaseReference usersRef;
    private StorageReference userProfileImageReference;
    private Spinner dropdown;
    private ArrayList<String> carList;
    private ArrayList<String> makeList;
  //  private Spinner modelSpinner;

    ProgressDialog loadingbar;

    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        username=(TextView)findViewById(R.id.setup_groupname);
        fullName=(TextView)findViewById(R.id.setup_group_location);
      //  modelSpinner= findViewById(R.id.car);
        dropdown = (Spinner)findViewById(R.id.setup_group_carname);
        loadingbar= new ProgressDialog(this);
        //hiding keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        userProfileImageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        profilePic=findViewById(R.id.setup_pic);


        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("profileimage").exists())
                {

                    Picasso.get().load(dataSnapshot.child("profileimage").getValue().toString()).into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        addMake();


    }


    public void saveUser(View view) {
if(username.getText().toString().isEmpty()||fullName.getText().toString().isEmpty())
{
    Toast.makeText(getApplicationContext(),"Please make sure all fields are filled",Toast.LENGTH_LONG).show();
}
else {
    Map<String, Object> userMap = new HashMap<>();
    userMap.put("username", username.getText().toString());
    userMap.put("fullname", fullName.getText().toString());
    userMap.put("car", dropdown.getSelectedItem().toString());
    userMap.put("dob", "Date here");
    userMap.put("status", "status here");
    userMap.put("location", "Canada");


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
                profilePic.setImageURI(resultUri);

                StorageReference filePath = userProfileImageReference.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetupActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
                            StorageReference filePath = userProfileImageReference.child(currentUserId + ".jpg");

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    usersRef.child("profileimage").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }







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


