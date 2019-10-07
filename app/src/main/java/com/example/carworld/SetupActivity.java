package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {


    private TextView username;
    private TextView fullName;
    private TextView car;
    private ImageView profilePic;
    final static int galleryPic=1;
    FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference userProfileImageReference;

    ProgressDialog loadingbar;

    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        username=findViewById(R.id.username);
        fullName=findViewById(R.id.fullname);
        car=findViewById(R.id.car);
        loadingbar= new ProgressDialog(this);
        profilePic=findViewById(R.id.displaypic);
        mAuth=FirebaseAuth.getInstance();
        userProfileImageReference=FirebaseStorage.getInstance().getReference().child("Profile Images");

        currentUserId=mAuth.getCurrentUser().getUid();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);


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
    userMap.put("country", "c300");


    usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
                sendToNews();
            } else {
                String message = task.getException().getMessage();
                Toast.makeText(getApplicationContext(), "ERROR:" + message, Toast.LENGTH_SHORT).show();
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
        startActivityForResult(galleryIntent,galleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==galleryPic && resultCode == RESULT_OK && data!=null)
        {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (requestCode ==RESULT_OK)
            {
                loadingbar.setTitle("Profile Image");
                loadingbar.setMessage("Please wait, while we are upating your profile image");
                loadingbar.show();
                loadingbar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();
                StorageReference filePath = userProfileImageReference.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(getApplicationContext(), "Image added", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().toString();
                            loadingbar.dismiss();

                            usersRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Intent setupIntent = new Intent (SetupActivity.this, SetupActivity.class);
                                        startActivity(setupIntent);
                                        Toast.makeText(getApplicationContext(), "Image stored", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }

                                    else
                                    {

                                        String message = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(), "ERROR:" + message , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }

                    }
                });
            }
            else
            {
                loadingbar.dismiss();

                Toast.makeText(getApplicationContext(), "ERROR: crop not working" , Toast.LENGTH_SHORT).show();


            }
        }
    }
}
