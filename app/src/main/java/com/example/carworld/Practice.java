package com.example.carworld;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Practice {
    private static FirebaseAuth mAuth;
    private static DatabaseReference groupref;
    private String PostKey;
    public static void main (String[]args){


        groupref= FirebaseDatabase.getInstance().getReference().child("Groups").child("Txi437sPsvhZG8CXgAsZJT7U019204-November-201910:42");
        mAuth= FirebaseAuth.getInstance();


        groupref.child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    System.out.println(user.getFullname());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    }

