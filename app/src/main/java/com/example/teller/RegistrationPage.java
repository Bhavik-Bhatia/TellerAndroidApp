package com.example.teller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationPage extends AppCompatActivity {

    TextView signinlink;
    Button register;
    TextInputLayout fullname,register_email,register_password,register_confirm_password;
    FirebaseAuth mAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.activity_registration_page);


        //INIT
        fullname = findViewById(R.id.fullname);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_confirm_password = findViewById(R.id.register_confirm_password);
        signinlink = findViewById(R.id.signinlink);

        //ONCLICK GOES TO SIGN IN ACTIVITY
        signinlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

    //ON CLICK REGISTER BUTTON USER DATA IS STORED IN FIREBASE
        register = findViewById(R.id.Register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegistrationPage.this);
                pd.setMessage("Please Wait....");
                pd.show();

                String fullname_text = fullname.getEditText().getText().toString();
                String email_text = register_email.getEditText().getText().toString();
                String password_text = register_password.getEditText().getText().toString();
                String confirm_password_text = register_confirm_password.getEditText().getText().toString();

                if(!(fullname_text.equals("") || email_text.equals("") || password_text.equals("") || confirm_password_text.equals(""))){
                        if (Patterns.EMAIL_ADDRESS.matcher(email_text).matches()){
                            if (password_text.equals(confirm_password_text)) {

                                mAuth = FirebaseAuth.getInstance();

                                //Creating the User in Firebase
                                mAuth.createUserWithEmailAndPassword(email_text, password_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            //NOW WE ADD THE DETAILS IN FIREBASE DATABASE
                                                  Add_Data_Database(fullname_text);
                                        } else {
                                            String error = task.getException().getMessage();
                                            pd.dismiss();
                                            Toast.makeText(getApplicationContext(), "Error:" + error, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            else {
                                pd.dismiss();
                                register_password.setError("Should be Same!");
                                register_confirm_password.setError("Should be Same!");
                            }
                        }
                        else{
                            pd.dismiss();
                            register_email.setError("Enter valid email address!");
                        }
                }
                else {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Please Fill Fields!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //METHOD TO ADD DATA IN DATABASE
    public void Add_Data_Database(String fullname_text){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        //WE GET ID AND EMAIL OF THE USER CURRENTLY SIGNED IN
        String uid = firebaseUser.getUid();
        String u_email = firebaseUser.getEmail();

        //In FIREBASE DATABASE WE CREATE A CHILD USERS AND INSIDE WE CREATE A SUB CHILD WITH USER ID IN WHICH WE WILL
        // ADD DATA

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("ID",uid);
        hashMap.put("email",u_email);
        hashMap.put("fullname",fullname_text);
        hashMap.put("bio",""); //WILL BE ADDED LATER
        hashMap.put("profile_img","https://firebasestorage.googleapis.com/v0/b/teller-28342.appspot.com/o/man.png?alt=media&token=18a831ea-d3fb-4a1f-9057-20878554b4e3");

        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //When the values are successfully added in Users Collection
                if(task.isSuccessful()){
                    pd.dismiss();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }else{
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Error: Could not be registered", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    ///......IF USER LOGGED IN GO TO HOME PAGE JAVA......
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        if(current_user != null){
            // ----- User will be redirected to HOME PAGE ------
            startActivity(new Intent(getApplicationContext(),HomePage.class));
            finish();
        }
    }

}