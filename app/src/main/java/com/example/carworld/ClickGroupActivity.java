package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import java.util.Map;

public class ClickGroupActivity extends AppCompatActivity {


    private ImageView PostImage;
    private TextView GroupLocation,GroupName,GroupStatus,CarName;
    private Button DeletePostButton,EditPostButton,DoneEditButton,JoinGroupButton,LeaveGroupButton;
    private String PostKey,currentUserID,databaseUserID,image,groupownerid;
    private DatabaseReference ClickPostRef,UserRef;
    private FirebaseAuth mAuth;
    private Spinner dropdown;
    private ArrayList<String> carList;
    private ArrayList<String> makeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_group);
        //hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        PostKey= getIntent().getExtras().get("PostKey").toString();
        groupownerid=getIntent().getExtras().get("groupownerid").toString();
        mAuth= FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();


        ClickPostRef= FirebaseDatabase.getInstance().getReference().child("Groups").child(PostKey);
        UserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        dropdown = (Spinner)findViewById(R.id.click_group_carname);
        dropdown.setEnabled(false);
        dropdown.setVisibility(View.INVISIBLE);

        CarName=(TextView)findViewById(R.id.click_group_carnametext);
        CarName.setEnabled(false);
     //   PostImage=(ImageView) findViewById(R.id.click_post_image2);
        GroupLocation=(TextView) findViewById(R.id.click_group_location);
        GroupLocation.setEnabled(false);
        GroupStatus=(TextView) findViewById(R.id.click_group_status);
        GroupStatus.setEnabled(false);

        GroupName=(TextView) findViewById(R.id.click_group_name);
        GroupName.setEnabled(false);
        DeletePostButton=(Button)findViewById(R.id.delete_post_btn2);
        EditPostButton=(Button)findViewById(R.id.edit_post_btn2);
      DoneEditButton=(Button)findViewById(R.id.edit_done_button);


      JoinGroupButton=(Button)findViewById(R.id.join_group_button);
        LeaveGroupButton=(Button)findViewById(R.id.leave_group_button);
        LeaveGroupButton.setVisibility(View.INVISIBLE);

// Toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Group Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addMake();


ClickPostRef.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.child("Members").hasChild(currentUserID))
        {
            LeaveGroupButton.setVisibility(View.VISIBLE);
            JoinGroupButton.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});




        DoneEditButton.setVisibility(View.INVISIBLE);
        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButton.setVisibility(View.INVISIBLE);

        ClickPostRef.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    final String location = dataSnapshot.child("grouplocation").getValue().toString();
                    String groupcar = dataSnapshot.child("groupcar").getValue().toString();
                    String groupname=dataSnapshot.child("groupname").getValue().toString();
                    String groupstatus=dataSnapshot.child("groupstatus").getValue().toString();
                    //        image=dataSnapshot.child("groupimage").getValue().toString();
                    if(dataSnapshot.hasChild("ownerid"))
                    databaseUserID = dataSnapshot.child("ownerid").getValue().toString();


                    GroupStatus.setText(groupstatus);

                   GroupLocation.setText(location);
                   GroupName.setText(groupname);
                   CarName.setText(groupcar);

                    ArrayAdapter carAdap= (ArrayAdapter)dropdown.getAdapter();
                    dropdown.setSelection(carAdap.getPosition(groupcar));

                    dropdown.setSelection(getIndex(dropdown,groupcar));


         //     Picasso.get().load(image).into(PostImage);
                    if (currentUserID.equals(databaseUserID)) {

                        DeletePostButton.setVisibility(View.VISIBLE);
                        EditPostButton.setVisibility(View.VISIBLE);
                        JoinGroupButton.setVisibility(View.INVISIBLE);
                        LeaveGroupButton.setVisibility(View.INVISIBLE);
                    }

                    EditPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DeletePostButton.setVisibility(View.INVISIBLE);
                            EditPostButton.setVisibility(View.INVISIBLE);
                            DoneEditButton.setVisibility(View.VISIBLE);
                            dropdown.setVisibility(View.VISIBLE);
                            CarName.setVisibility(View.INVISIBLE);
                            //EditCurrentPost(description);
                            GroupLocation.setEnabled(true);
                            GroupStatus.setEnabled(true);
                            dropdown.setEnabled(true);
                            GroupName.setEnabled(true);
                            CarName.setEnabled(true);
                        }
                    });

                    DoneEditButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dropdown.setVisibility(View.INVISIBLE);
                            DeletePostButton.setVisibility(View.VISIBLE);

                            GroupName=(TextView) findViewById(R.id.click_group_name);
                            GroupLocation=(TextView) findViewById(R.id.click_group_location);
                            GroupStatus=(TextView) findViewById(R.id.click_group_status);
                            CarName=(TextView)findViewById(R.id.click_group_carnametext);
                            ClickPostRef.child("groupname").setValue(GroupName.getText().toString());
                            ClickPostRef.child("grouplocation").setValue(GroupLocation.getText().toString());
                            ClickPostRef.child("groupstatus").setValue(GroupStatus.getText().toString());
                            ClickPostRef.child("groupcar").setValue(CarName.getText().toString());

                            if(dropdown.getSelectedItem()!=null)
                            ClickPostRef.child("groupcar").setValue(dropdown.getSelectedItem().toString());

                            EditPostButton.setVisibility(View.VISIBLE);
                            DoneEditButton.setVisibility(View.INVISIBLE);
                            CarName.setVisibility(View.VISIBLE);

                        GroupLocation.setEnabled(false);
                        GroupStatus.setEnabled(false);
                        GroupName.setEnabled(false);
                        dropdown.setEnabled(false);
                            CarName.setEnabled(false);



                        }
                    });



                    JoinGroupButton.setOnClickListener(new View.OnClickListener() {


                        public void onClick(View view) {

                            UserRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Map<String, Object> userMap = new HashMap<>();
                                    Map<String, Object> map = new HashMap<>();

                                    map.put(currentUserID,currentUserID);

                                    userMap.put("username", dataSnapshot.child(currentUserID).child("username").getValue().toString());
                                    userMap.put("fullname", dataSnapshot.child(currentUserID).child("fullname").getValue().toString());
                                    userMap.put("car", dataSnapshot.child(currentUserID).child("car").getValue().toString());
                                    userMap.put("dob", dataSnapshot.child(currentUserID).child("dob").getValue().toString());
                                    userMap.put("status", dataSnapshot.child(currentUserID).child("status").getValue().toString());
                                    userMap.put("location", dataSnapshot.child(currentUserID).child("location").getValue().toString());
                                    userMap.put("profileimage", dataSnapshot.child(currentUserID).child("profileimage").getValue().toString());
                                  ClickPostRef.updateChildren(map);
                                    ClickPostRef.child("Members").child(currentUserID).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Group Joined", Toast.LENGTH_SHORT).show();

                                                sendUserToGroups();
                                            } else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(getApplicationContext(), "ERROR:" + message, Toast.LENGTH_LONG).show();

                                            }
                                        }


                                    });




                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    });

                    LeaveGroupButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ClickPostRef.child(currentUserID).removeValue();
                            ClickPostRef.child("Members").child(currentUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(ClickGroupActivity.this, "Left Group ",Toast.LENGTH_SHORT);
                                    sendUserToGroups();
                                    sendUserToMyGroups();

                                }
                            });


                        }
                    });
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteCurrentPost();
            }
        });

    }

    // Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home)
        {
            Intent clickPostIntent = new Intent (ClickGroupActivity.this
                    , ViewGroupsActivity.class);

            startActivity(clickPostIntent);

        }
        return super.onOptionsItemSelected(item);

    }


    /*
        private void EditCurrentPost(String description) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ClickGroupActivity.this);
            builder.setTitle("Edit Post");


            final EditText inputField= new EditText(ClickGroupActivity.this);
            inputField.setText(description);
            builder.setView(inputField);


            builder.setPositiveButton("Update Button", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ClickPostRef.child("grouplocation").setValue(inputField.getText().toString());
                    Toast.makeText(ClickGroupActivity.this, "Post Updated Successfully.. ",Toast.LENGTH_SHORT);

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.background_light);

        }
        */
    private void DeleteCurrentPost() {
        ClickPostRef.removeValue();
        sendUserToGroups();
        Toast.makeText(this,"Group has been deleted",Toast.LENGTH_SHORT).show();


    }
    public void sendUsertoNews()
    {
        Intent intent = new Intent (this,NewsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToGroups() {
        Intent intent = new Intent (this,ViewGroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void sendOwnerToMembers() {
        Intent intent = new Intent (this,ManageMembersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("PostKey",PostKey);
        // clickPostIntent.putExtra("Car",car);


        startActivity(intent);
        finish();
    }
    private void sendUserToMembers() {
        Intent intent = new Intent (this,GroupMembersActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("PostKey",PostKey);
        intent.putExtra("groupownerid",groupownerid);
        // clickPostIntent.putExtra("Car",car);


        startActivity(intent);
        finish();
    }
    private void sendUserToMyGroups() {
        Intent intent = new Intent (this,MyGroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    public void showMembers(View view) {

sendUserToMembers();
    }


}