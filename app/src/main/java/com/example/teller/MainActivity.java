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

public class MainActivity extends AppCompatActivity {

    TextView signuplink;
    TextView forgotpassword;
    Button Login;
    TextInputLayout login_email,login_pass;
    FirebaseAuth mAuth;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);

        //INITIALIZE
        signuplink = findViewById(R.id.signuplink);
        forgotpassword = findViewById(R.id.forgotpassword);
        Login = findViewById(R.id.Login);
        login_email = findViewById(R.id.login_email);
        login_pass = findViewById(R.id.login_pass);

        //CLICK ON SIGN UP GOES TO SIGN UP ACTIVITY
         signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),RegistrationPage.class);
                startActivity(i);
            }
        });

         ///GOES TO F PASSWORD ACTIVITY
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),forgotpassword1.class);
                startActivity(i);
            }
        });

        ///---- ONCICK LOGIN ---------
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Please Wait....");
                pd.show();

                String email_text = login_email.getEditText().getText().toString();
                String pass_text = login_pass.getEditText().getText().toString();

                if(!(email_text.equals("") || pass_text.equals(""))){
                    //EMAIL PATTERN IS RIGHT
                    if(Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
                        mAuth = FirebaseAuth.getInstance();

                        //IF NOT EMPTY THEN SIGN IN WITH EMAIL AND PASSWORD
                        mAuth.signInWithEmailAndPassword(email_text, pass_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    pd.dismiss();
                                    startActivity(new Intent(getApplicationContext(),HomePage.class));
                                    finish();
                                } else {
                                    String error = task.getException().getMessage();
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "Error:" + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                            pd.dismiss();
                          login_email.setError("Enter valid email address!");
                    }
                }else{
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Please Fill Fields!",Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        // make sure you have this outcommented
        // super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}