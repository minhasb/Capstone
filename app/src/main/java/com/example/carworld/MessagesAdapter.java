package com.example.carworld;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>
        {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
private DatabaseReference usersDatabaseRef;

public MessagesAdapter(List<Messages> userMessagesList){

    this.userMessagesList=userMessagesList;
}


            public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView senderMessageText;
    public TextView recieverMessageText;
    public CircleImageView recieverProfileImage;

    public MessageViewHolder(@NonNull View itemView) {
                    super(itemView);
                    senderMessageText=(TextView) itemView.findViewById(R.id.sender_message_text);
        senderMessageText=(TextView) itemView.findViewById(R.id.reciever_message_text);
        recieverProfileImage=(CircleImageView) itemView.findViewById(R.id.message_profile_image);
                }


            }


            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View V= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layoutof_users,parent,false);

                mAuth=FirebaseAuth.getInstance();

                return new MessageViewHolder(V);


            }

            @Override
            public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
                String messageSenderID=mAuth.getCurrentUser().getUid();
                Messages messages =userMessagesList.get(position);

                final CircleImageView userimage = holder.recieverProfileImage;

                String fromUserId=messages.getFrom();

                String fromMessageType= messages.getType();

                usersDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
                usersDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if(dataSnapshot.exists())
                      {
                          String image =dataSnapshot.child("profileimage").getValue().toString();
                          Picasso.get().load(image).into(userimage);


                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if(fromMessageType.equals("text"))
                {
                    holder.recieverMessageText.setVisibility(View.INVISIBLE);
                    holder.recieverProfileImage.setVisibility(View.INVISIBLE);


                    if (fromUserId.equals(messageSenderID))
                    {
                        holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                        holder.senderMessageText.setTextColor(Color.WHITE);
                        holder.senderMessageText.setGravity(Gravity.LEFT);
                        holder.senderMessageText.setText(messages.getMessage());

                    }

                    else
                    {
                        holder.senderMessageText.setVisibility(View.INVISIBLE);
                        holder.recieverMessageText.setVisibility(View.VISIBLE);
                        holder.recieverProfileImage.setVisibility(View.VISIBLE);


                        holder.recieverMessageText.setBackgroundResource(R.drawable.reciever_message_text_background);
                        holder.recieverMessageText.setTextColor(Color.WHITE);
                        holder.recieverMessageText.setGravity(Gravity.LEFT);
                        holder.recieverMessageText.setText(messages.getMessage());
                    }
                }

            }

            @Override
            public int getItemCount() {
                return userMessagesList.size();
            }
        }

