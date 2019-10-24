package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsActivity extends AppCompatActivity {
    private Query query;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostsRef;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        query = FirebaseDatabase.getInstance().getReference().child("Posts");
        drawerLayout=(DrawerLayout) findViewById(R.id.drawable_layout);
        navigationView=(NavigationView) findViewById(R.id.navigation_view);
        View navView =navigationView.inflateHeaderView(R.layout.navigation_header);

        postList = (RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        DisplayAllUsersPosts();

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

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();
        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder postsViewHolder, int position, @NonNull Posts posts) {
                postsViewHolder.setFullname(posts.getFullname());
                postsViewHolder.setDescription(posts.getDescription());
                postsViewHolder.setProfileImage(getApplicationContext(),posts.getProfileimage());
                postsViewHolder.setPostImage(getApplicationContext(),posts.getPostimage());
                postsViewHolder.setDate(posts.getDate());
                postsViewHolder.SetTime(posts.getTime());
            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_posts_layout,parent,false);
                return new PostsViewHolder(view);
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
            TextView username = (TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }

        public void setProfileImage (Context ctx, String profileimage){
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).into(image);

        }

        public void SetTime (String time){
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("     "+time);
        }

        public void setDate (String date){
            TextView postDate = (TextView) mView.findViewById(R.id.post_date);
            postDate.setText("     "+date);
        }

        public void setDescription (String description){
            TextView postDescription = (TextView) mView.findViewById(R.id.post_description);
            postDescription.setText(description);
        }

        public void setPostImage (Context ctx1, String postImage){
            ImageView postImages = (ImageView) mView.findViewById(R.id.post_image);
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
                    Toast.makeText(getApplicationContext(),"Welcome" ,Toast.LENGTH_SHORT).show();
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

                Intent intent = new Intent(this,ProfileActivity.class);
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


    }
}
