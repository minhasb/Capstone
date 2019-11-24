package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendMessageActivity extends AppCompatActivity {
    private String recieverId,senderid, saveCurrentDate, saveCurrentTime, messageRandomName,sender;
    private TextView message,recievername;
    private CircleImageView recieverProfileImage;
    private RecyclerView userMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,SenderRef,RecieverRef,RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        recieverId=getIntent().getExtras().get("userid").toString();
        mAuth= FirebaseAuth.getInstance();
        senderid=mAuth.getCurrentUser().getUid();
        RootRef=FirebaseDatabase.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        SenderRef=UsersRef.child(senderid);
        RecieverRef=UsersRef.child(recieverId);
        initializeFields();



        FetchMessages();    

    }

    private void FetchMessages() {
              senderid=mAuth.getCurrentUser().getUid();

               recieverId=getIntent().getExtras().get("userid").toString();

                  Toast.makeText(SendMessageActivity.this,"Fetching Messages", Toast.LENGTH_SHORT);
        RootRef.child("Messages").child(senderid).child(recieverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                       if(dataSnapshot.exists()){

                               Toast.makeText(SendMessageActivity.this,"Message Exists", Toast.LENGTH_SHORT);
                           Messages messages= dataSnapshot.getValue(Messages.class);

                           messagesList.add(messages);

                           messageAdapter.notifyDataSetChanged();
                       }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void initializeFields(){

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle("Send Message");
        setSupportActionBar(toolbar);

        ActionBar actionBar=getSupportActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view =layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);

        message= findViewById(R.id.messageText);
        recievername=findViewById(R.id.custom_profile_name);


        recievername.setText(getIntent().getExtras().get("username").toString());

        recieverProfileImage=findViewById(R.id.custom_profile_image);
        Picasso.get().load(getIntent().getExtras().get("userimage").toString()).into(recieverProfileImage);


        messageAdapter = new MessageAdapter(messagesList);
       userMessageList= (RecyclerView) findViewById(R.id.messages_list_users) ;
       linearLayoutManager= new LinearLayoutManager(this);
       userMessageList.setHasFixedSize(true);
       userMessageList.setLayoutManager(linearLayoutManager);
       userMessageList.setAdapter(messageAdapter);


    }

    public void sendMessage(View view) {
                            
        if (TextUtils.isEmpty(message.getText().toString())) {
            Toast.makeText(this, "Please fill out a message before sending", Toast.LENGTH_SHORT);
        } else {


            SenderRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    sender = dataSnapshot.child("username").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            String message_sender_ref= "Messages/" + senderid + "/" + recieverId;
           String message_reciever_ref= "Messages/" + recieverId + "/" + senderid;

           DatabaseReference user_message_key=RootRef.child("Messages").child(senderid).child(recieverId).push();

           String message_push_id=user_message_key.getKey();

            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            saveCurrentTime = currentTime.format(calFordDate.getTime());



            HashMap messageTextbody = new HashMap();
            messageTextbody.put("message", message.getText().toString());
            messageTextbody.put("time", saveCurrentTime);
            messageTextbody.put("date", saveCurrentDate);
            messageTextbody.put("type", "text");
            messageTextbody.put("from", senderid);

            HashMap messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref+"/"+message_push_id, messageTextbody)  ;
            messageBodyDetails.put(message_reciever_ref+"/"+message_push_id, messageTextbody)  ;

        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                
                 Toast.makeText(SendMessageActivity.this,"Message Sent successfully",Toast.LENGTH_SHORT);
                 message.setText("");
            }

          

           
        } );


        }
    }
}
