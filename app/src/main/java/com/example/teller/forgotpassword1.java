package com.example.teller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword1 extends AppCompatActivity {

    Button send_email_button;
    TextInputLayout reg_email;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_forgotpassword1);

        send_email_button = findViewById(R.id.sent_email_button);
        reg_email = findViewById(R.id.registered_email);


        send_email_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_text = reg_email.getEditText().getText().toString();

                if (!email_text.equals("")){
                    if (Patterns.EMAIL_ADDRESS.matcher(email_text).matches()){
                        //If all things are fine Send a EMAIL FOR PASSWORD CHANGE
                       mAuth = FirebaseAuth.getInstance();

                       mAuth.sendPasswordResetEmail(email_text).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Email sent",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }

                           }
                       });

                    }
                    else{
                        reg_email.setError("Input a valid email!");
                        reg_email.requestFocus();
                    }
                }
                else{
                    reg_email.setError("Fill the field!");
                    reg_email.requestFocus();
                }
            }

        });



    }
}