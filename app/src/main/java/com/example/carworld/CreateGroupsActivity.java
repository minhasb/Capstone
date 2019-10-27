package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CreateGroupsActivity extends AppCompatActivity {


    private TextView groupname;
    private TextView grouplocation;

    private ImageView profilePic;
    final static int Gallery_Pick=1;
    FirebaseAuth mAuth;

    private DatabaseReference groupsRef;
    private StorageReference groupImageReference;
    private Spinner dropdown;
    private ArrayList<String> carList;
    private ArrayList<String> makeList;

    private String groupownerid;
    //  private Spinner modelSpinner;

    ProgressDialog loadingbar;

    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_groups);
        groupname=(TextView)findViewById(R.id.setup_groupname);
        grouplocation=(TextView)findViewById(R.id.setup_group_location);
        //  modelSpinner= findViewById(R.id.car);
        dropdown = (Spinner)findViewById(R.id.setup_group_carname);
        loadingbar= new ProgressDialog(this);
        profilePic=findViewById(R.id.setup_group_pic);
        mAuth=FirebaseAuth.getInstance();
        groupownerid=mAuth.getCurrentUser().getUid();
        groupImageReference= FirebaseStorage.getInstance().getReference().child("Group Images");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        currentUserId=mAuth.getCurrentUser().getUid();
        groupsRef= FirebaseDatabase.getInstance().getReference().child("Groups");

        addMake();


    }


    public void saveGroup(View view) {

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        String saveCurrentTime = currentTime.format(calFordDate.getTime());

      String postRandomName = groupownerid+ saveCurrentDate + saveCurrentTime;

        if(groupname.getText().toString().isEmpty()||grouplocation.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please make sure all fields are filled",Toast.LENGTH_LONG).show();
        }
        else {
            Map<String, Object> groupMap = new HashMap<>();
            groupMap.put("groupname", groupname.getText().toString());
            groupMap.put("grouplocation", grouplocation.getText().toString());
            groupMap.put("groupcar", dropdown.getSelectedItem().toString());
            groupMap.put("ownerid", groupownerid);


            groupsRef.child(postRandomName).updateChildren(groupMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                StorageReference filePath = groupImageReference.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(com.example.carworld.CreateGroupsActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
                            StorageReference filePath = groupImageReference.child(currentUserId + ".jpg");

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    groupsRef.child("groupimage").setValue(uri.toString());

                                    Picasso.get().load(uri.toString()).into((ImageView)findViewById(R.id.setup_group_pic));

                                    Toast.makeText(com.example.carworld.CreateGroupsActivity.this, "Group Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                    loadingbar.dismiss();
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
                System.out.println(model);
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

