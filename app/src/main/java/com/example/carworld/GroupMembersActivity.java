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

import java.security.acl.Group;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMembersActivity extends AppCompatActivity {

    private Query query;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostsRef,groupref;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;
    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private String currentUserID,PostKey,car,groupownerid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PostKey= getIntent().getExtras().get("PostKey").toString();
        groupref=FirebaseDatabase.getInstance().getReference().child("Groups").child(PostKey);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        query = groupref.child("Members");
        drawerLayout=(DrawerLayout) findViewById(R.id.drawable_layout);
        navigationView=(NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.header_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.header_username);



        postList = (RecyclerView) findViewById(R.id.all_group_members);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        DisplayAllUsersPosts();
//Toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Group Memebers");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                        Toast.makeText(GroupMembersActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
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


        groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                groupownerid=dataSnapshot.child("ownerid").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //Toolbar
@Override
    public boolean onOptionsItemSelected(MenuItem item){
    if(item.getItemId()==android.R.id.home)
    {
        Intent clickPostIntent = new Intent (GroupMembersActivity.this
                , ClickGroupActivity.class);

        groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                car= dataSnapshot.child("groupcar").getValue().toString();
                groupownerid=dataSnapshot.child("ownerid").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        clickPostIntent.putExtra("Car",car);
        clickPostIntent.putExtra("groupownerid",groupownerid);

        clickPostIntent.putExtra("PostKey",PostKey);

        startActivity(clickPostIntent);

    }
    return super.onOptionsItemSelected(item);

}


    private void DisplayAllUsersPosts() {


        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<User, GroupMembersActivity.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupMembersActivity.PostsViewHolder postsViewHolder, int position, @NonNull User user) {

               final String userId = getRef(position).getKey();

               final String car=user.getCar();
             final   String dob=user.getDob();
               final String fullname=user.getFullname();
                final String location= user.getLocation();
              final  String profileimage=user.getProfileimage();
              final  String status=user.getStatus();
              final  String username=user.getUsername();
                postsViewHolder.setFullname(user.getUsername());
      postsViewHolder.setProfileImage(getApplicationContext(),user.getProfileimage());

                postsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent clickPostIntent = new Intent (GroupMembersActivity.this
                                , ClickUserActivity.class);
                        clickPostIntent.putExtra("groupownerid",groupownerid);
                        clickPostIntent.putExtra("car",car);
                        clickPostIntent.putExtra("dob",dob);
                        clickPostIntent.putExtra("fullname",fullname);
                        clickPostIntent.putExtra("location",location);
                        clickPostIntent.putExtra("profileimage",profileimage);
                        clickPostIntent.putExtra("status",status);
                        clickPostIntent.putExtra("username",username);
                        clickPostIntent.putExtra("userid",userId);
                        clickPostIntent.putExtra("PostKey",PostKey);



                        startActivity(clickPostIntent);

                    }
                });

            }

            @NonNull
            @Override
            public GroupMembersActivity.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_users_layout,parent,false);
                return new GroupMembersActivity.PostsViewHolder(view);
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

    public static class PostsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFullname (String fullname){
            TextView username = (TextView) mView.findViewById(R.id.groupmemberuser_name);
            username.setText(fullname);
        }

        public void setProfileImage (Context ctx, String profileimage){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.groupmembers_profile_image);
            Picasso.get().load(profileimage).into(image);

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


    }




}
