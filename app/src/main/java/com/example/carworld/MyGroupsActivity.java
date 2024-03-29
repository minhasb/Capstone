package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGroupsActivity extends AppCompatActivity {


    private Query query;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,GroupsRef;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private String currentUserID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");


        //query = FirebaseDatabase.getInstance().getReference().child("Groups").orderByChild(currentUserID).equalTo(currentUserID);

         query = FirebaseDatabase.getInstance().getReference().child("Groups").orderByChild(currentUserID).equalTo(currentUserID);




       // query = FirebaseDatabase.getInstance().getReference().child("Groups");


        drawerLayout=(DrawerLayout) findViewById(R.id.drawable_layout);
        navigationView=(NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.header_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.header_username);

//Toolbar
        //Toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("My Groups");
        setSupportActionBar(toolbar);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        DisplayAllUsersPosts();


        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }
                    if(dataSnapshot.hasChild("profileimage"))
                    {
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).into(NavProfileImage);

                    }
                    else
                    {
                        Toast.makeText(MyGroupsActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                UserMenuSelector(menuItem);
                return false;
            }
        });
        onStart();


    }

    private void DisplayAllUsersPosts() {

        FirebaseRecyclerOptions<Groups> options = new FirebaseRecyclerOptions.Builder<Groups>().setQuery(query, Groups.class).build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Groups, MyGroupsActivity.PostsViewHolder>(options) {
            String car;

            @Override
            protected void onBindViewHolder(@NonNull MyGroupsActivity.PostsViewHolder postsViewHolder, int position, @NonNull Groups groups) {

                final String PostKey = getRef(position).getKey();
                final String OwnerId=groups.getOwnerid();

                postsViewHolder.setFullname(groups.getGroupname());
                postsViewHolder.setLocation("Location: "+ groups.getGrouplocation());
                postsViewHolder.setGroupCar("Group car:" + groups.getGroupcar());
                postsViewHolder.setGroupstatus(groups.getGroupstatus());
                postsViewHolder.setGroupImage(getApplicationContext(),groups.getGroupimage());



                postsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent clickPostIntent = new Intent (MyGroupsActivity.this
                                , ClickGroupActivity.class);

                        clickPostIntent.putExtra("groupownerid",OwnerId);
                        clickPostIntent.putExtra("PostKey",PostKey);


                       // clickPostIntent.putExtra("Car",car);
                        startActivity(clickPostIntent);

                    }
                });

            }

            @NonNull
            @Override
            public MyGroupsActivity.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_groups_layout,parent,false);
                return new MyGroupsActivity.PostsViewHolder(view);
            }
        };
        adapter.startListening();
        postList.setAdapter(adapter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        checkUserExistence();

    }

    public void searchGroup(View view) {
        Intent intent = new Intent (this,ViewGroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void createGroup(View view) {
        Intent intent = new Intent (this,CreateGroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFullname (String fullname){
            TextView username = (TextView) mView.findViewById(R.id.click_group_name);
            username.setText(fullname);
        }


        public void setLocation (String location){
            TextView postDescription = (TextView) mView.findViewById(R.id.click_group_location);
            postDescription.setText(location);
        }

        public void setGroupCar(String car){
            TextView groupCar = (TextView) mView.findViewById(R.id.click_group_car);
            groupCar.setText(car);
        }

        public void setGroupstatus (String status){
            TextView groupStatus = (TextView) mView.findViewById(R.id.click_group_status);
            groupStatus.setText(status);
        }

        public void setGroupImage (Context ctx1, String postImage){
            ImageView postImages = (ImageView) mView.findViewById(R.id.click_post_image2);
            Picasso.get().load(postImage).into(postImages);
        }


    }
    private void checkUserExistence(){

        final String current_user_id=mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild(current_user_id))
                {
                    sendUsertoLogin();
                }

                else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void logout(View view) {

        mAuth.signOut();
        sendUsertoLogin();
    }

    public void sendUsertoLogin()
    {
        Intent intent = new Intent (this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }




    private void UserMenuSelector(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_logout:

                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
        }
        switch (item.getItemId())
        {
            case R.id.nav_profile:

                Intent intent = new Intent(this,UserProfile.class);
                startActivity(intent);
                break;
        }
        switch (item.getItemId())
        {
            case R.id.nav_post:

                Intent intent = new Intent(this,PostActivity.class);
                startActivity(intent);
                break;
        }
        switch (item.getItemId())
        {
            case R.id.nav_settings:

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        switch (item.getItemId())
        {
            case R.id.nav_groups:

                Intent intent = new Intent(this, GroupsActivity.class);
                startActivity(intent);
                break;
        }
        switch (item.getItemId())
        {
            case R.id.nav_all_posts:

                Intent intent = new Intent(this, NewsActivity.class);
                startActivity(intent);
                break;
        }


    }



}