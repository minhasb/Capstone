package com.example.carworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GroupsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
    }

    public void createGroup(View view) {
        SendToCreateGroup();
    }

    public void viewGroups(View view){SendToViewGroups();}

    public void myGroups(View view){SendToMyGroups();}

    private void SendToCreateGroup() {

        Intent intent = new Intent (this,CreateGroupsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

    }

    private void SendToViewGroups() {

        Intent intent = new Intent (this,ViewGroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
    private void SendToMyGroups() {

        Intent intent = new Intent (this,MyGroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

}
