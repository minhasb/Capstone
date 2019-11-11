package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageMembersActivity extends AppCompatActivity {
private FirebaseAuth mAuth;
private DatabaseReference groupref;
private String PostKey,SelectedMember;
private Button GoBackBtn,DeleteBtn;
private ArrayList<User> groupmembers;
private ArrayList<String> groupmembersnames;
    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_members);

        groupmembersnames=new ArrayList<>();
        groupmembers=new ArrayList<>();
        System.out.println("started");

        GoBackBtn=(Button)findViewById(R.id.goback);
        DeleteBtn=(Button)findViewById(R.id.deleteuser);
        PostKey= getIntent().getExtras().get("PostKey").toString();
        dropdown=findViewById(R.id.member_names);

        System.out.println(PostKey);
        groupref=FirebaseDatabase.getInstance().getReference().child("Groups").child(PostKey);
        mAuth= FirebaseAuth.getInstance();

        groupref.child("Members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //System.out.println(dataSnapshot.getValue().toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    groupmembers.add(user);
                    groupmembersnames.add(user.getFullname());


                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ArrayAdapter<String> membersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupmembersnames);
        membersAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        dropdown.setAdapter(membersAdapter);

        GoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUsertoGroups();
            }
        });

        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (dropdown.getSelectedItem() != null) {
                    SelectedMember = dropdown.getSelectedItem().toString();


                    groupref.child("Members").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //System.out.println(dataSnapshot.getValue().toString());
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User user = snapshot.getValue(User.class);
                                if (user.getFullname() == SelectedMember) {
                                    groupref.child("Members").child(snapshot.getKey().toString()).removeValue();
                                    Toast.makeText(getApplicationContext(),"Member deleted from group", Toast.LENGTH_LONG).show();
                                    sendUsertoGroups();
                                }


                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please make sure a user is selected", Toast.LENGTH_LONG).show();
                }
            }

        });

        // Toolbar being added
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Managing group Members");
        setSupportActionBar(toolbar);

    }

    public void sendUsertoGroups()
    {
        Intent intent = new Intent (this,MyGroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
