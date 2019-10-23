package com.example.carworld;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    TextView useremail;
    TextView userpassword;
    FirebaseAuth mAuth;
    ProgressDialog loadingbar;
    private DatabaseReference userref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingbar= new ProgressDialog(this);
        userref= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
    

    }








    public void logincheck(View view) {

        useremail=findViewById(R.id.email);
        userpassword=findViewById(R.id.password);

        if (useremail.getText().toString().isEmpty() || userpassword.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please make sure both fields are filled",Toast.LENGTH_LONG).show();
    }

        else {

            loadingbar.setTitle("Logging in");
            loadingbar.setMessage("Please wait, while you are being logged in");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(useremail.getText().toString(), userpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loadingbar.dismiss();
                        final String current_user_id=mAuth.getCurrentUser().getUid();

                        userref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(!dataSnapshot.hasChild(current_user_id))
                                {
                                    sendUsertoSetup();
                                }
                                else if(!dataSnapshot.child(current_user_id).hasChild("username")){

                                        sendUsertoSetup();


                                    }

                                else
                                {

                                    sendUsertoNews();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Toast.makeText(getApplicationContext(), "LOGIN SUCCESSFUL", Toast.LENGTH_SHORT).show();


                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), "ERROR:" + message, Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }
                }
            });
        }}



    private void checkUserExistence(){

        final String current_user_id = mAuth.getCurrentUser().getUid();

        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(!dataSnapshot.hasChild(current_user_id))
                {
                    sendUsertoNews();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void sendUsertoSetup()
    {
        Intent intent = new Intent (this,SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void sendUsertoLogin()
    {
        Intent intent = new Intent (this,SetupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    public void sendUsertoNews()
    {
        Intent intent = new Intent (this,NewsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


public void onRegister(View view){
    Intent intent = new Intent (this,RegisterActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intent);
    finish();
}




}
