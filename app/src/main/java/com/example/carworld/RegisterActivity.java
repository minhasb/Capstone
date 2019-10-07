package com.example.carworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    TextView useremail;
    TextView userpassword;
    TextView userconfirmpass;
    FirebaseAuth mAuth;

    ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        userconfirmpass= findViewById(R.id.confirmpassword);
        useremail = findViewById(R.id.email);
        userpassword= findViewById(R.id.password);
        loadingbar= new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
    }

    public void onRegister(View view) {


        if(useremail.getText().toString().isEmpty() || userpassword.getText().toString().isEmpty() || userconfirmpass.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Please enter both fields",Toast.LENGTH_LONG).show();

        }

        else if (!userpassword.getText().toString().equals(userconfirmpass.getText().toString()))
        {

            Toast.makeText(getApplicationContext(),"Make sure both passwords match",Toast.LENGTH_LONG).show();
        }

        else if (!isValid(useremail.getText().toString()))
        {
            Toast.makeText(getApplicationContext(),"Make sure the email is of valid format",Toast.LENGTH_LONG).show();
        }

        else if (userpassword.getText().toString().length()<=6 || userconfirmpass.getText().toString().length()<=6)
        {
            Toast.makeText(getApplicationContext(),"Make sure password length is atleast 7 characters",Toast.LENGTH_LONG).show();

        }
        else {
            loadingbar.setTitle("Creating new account");
            loadingbar.setMessage("Please wait, while we are creating your new account");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(useremail.getText().toString(),userpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Account Created",Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                        sendToSetup();

                }

                    else
                    {
                        String message = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(),"ERROR:" + message ,Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();

                    }
            }












});

        }
    }

    public void sendToSetup(){

        Intent setup = new Intent(this,SetupActivity.class);
        setup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setup);
        finish();
    }


    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void goBack(View view) {

        sendToHome();
    }

    private void sendToHome() {
        Intent setup = new Intent(this,MainActivity.class);
        setup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setup);
        finish();
    }

}