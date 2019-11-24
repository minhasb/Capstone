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

    private ImageView groupPic;
    final static int Gallery_Pick=1;
    FirebaseAuth mAuth;
    private Uri ImageUri;

    private DatabaseReference groupsRef,UsersRef;
    private StorageReference groupImageReference;
    private String downloadUrl;
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
        groupPic=findViewById(R.id.setup_group_pic);
        mAuth=FirebaseAuth.getInstance();
        groupownerid=mAuth.getCurrentUser().getUid();
        groupImageReference= FirebaseStorage.getInstance().getReference().child("Group Images");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        currentUserId=mAuth.getCurrentUser().getUid();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        groupsRef= FirebaseDatabase.getInstance().getReference().child("Groups");

        addMake();


    }


    public void saveGroup(View view) {
saveGroupInformation();

    }

    private void saveGroupInformation() {


        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        String saveCurrentTime = currentTime.format(calFordDate.getTime());

        final String postRandomName = groupownerid+ saveCurrentDate + saveCurrentTime;

        UsersRef.addValueEventListener(new ValueEventListener() {
            Map<String, Object> userMap = new HashMap<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userMap.put("username", dataSnapshot.child(groupownerid).child("username").getValue().toString());
                userMap.put("fullname", dataSnapshot.child(groupownerid).child("fullname").getValue().toString());
                userMap.put("car", dataSnapshot.child(groupownerid).child("car").getValue().toString());
                userMap.put("dob", dataSnapshot.child(groupownerid).child("dob").getValue().toString());
                userMap.put("status", dataSnapshot.child(groupownerid).child("status").getValue().toString());
                userMap.put("location", dataSnapshot.child(groupownerid).child("location").getValue().toString());
                userMap.put("profileimage", dataSnapshot.child(groupownerid).child("profileimage").getValue().toString());
                groupsRef.child(postRandomName).child("Members").child(currentUserId).updateChildren(userMap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(groupname.getText().toString().isEmpty()||grouplocation.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please make sure all fields are filled",Toast.LENGTH_LONG).show();
        }
        else {
            Map<String, Object> groupMap = new HashMap<>();

            groupMap.put(groupownerid,groupownerid);


            groupMap.put("groupname", groupname.getText().toString());
            groupMap.put("grouplocation", grouplocation.getText().toString());
            groupMap.put("groupcar", dropdown.getSelectedItem().toString());
            groupMap.put("groupstatus", "Enter Status here");
            groupMap.put("ownerid", groupownerid);
            groupMap.put("groupimage", downloadUrl);



            groupsRef.child(postRandomName).updateChildren(groupMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Group Created", Toast.LENGTH_SHORT).show();
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
            ImageUri = data.getData();
            groupPic.setImageURI(ImageUri);
        }

        StoringImageToFirebaseStorage();
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




    private void StoringImageToFirebaseStorage()
    {

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        String saveCurrentTime = currentTime.format(calFordDate.getTime());

        final String postRandomName = groupownerid+ saveCurrentDate + saveCurrentTime;

        StorageReference filePath = groupImageReference.child("Group Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    StorageReference filePath = groupImageReference.child("Group Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Toast.makeText(CreateGroupsActivity.this, "image uploaded successfully to Storage...", Toast.LENGTH_SHORT).show();
                            downloadUrl=uri.toString();
                            saveGroupInformation();
                        }
                    });


                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(CreateGroupsActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    public void goBack(View view){

        Intent setup = new Intent(this,GroupsActivity.class);
        setup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setup);
        finish();
    }


}

