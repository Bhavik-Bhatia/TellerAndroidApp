package com.example.teller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserPostsAudioListPage extends AppCompatActivity {
    ImageView BackToHome;

    TextView Librarygetname;

    List<UserPostsAudiodetails> userPostsAudiodetails;

    UserPostsAudiodetails userPostsAudiodetailsobj;

    RecyclerView rv_audiolist;
    CardView Audiocardview;

    UserPostAudioListAdapter ad;
    FirebaseAuth mAuth;

    String library_ID;
    String Library_name;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.activity_user_posts_audio_list_page);

        pd = new ProgressDialog(UserPostsAudioListPage.this);

        //GETTING DATA FROM LIBRARY FRAGMENT
        Intent i = getIntent();

        Librarygetname = findViewById(R.id.Librarygetname);

        ///GETTING CURRENT USER
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();


        SharedPreferences sh = getSharedPreferences("MySharedPref",0);

        if(i.getStringExtra("com.example.imusic.NotificationFragment.lib_name") != null){

            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            myEdit.putString("library_name",i.getStringExtra("com.example.imusic.NotificationFragment.lib_name"));
            myEdit.putString("library_id",i.getStringExtra("com.example.imusic.NotificationFragment.lib_id"));

            myEdit.commit();
            myEdit.apply();

        }
        if(sh != null){

            Library_name = sh.getString("library_name", "");
            library_ID = sh.getString("library_id", "");

            Librarygetname.setText(Library_name);
        }

        //RV with Card View
        //Creating librarydetails object and setting values using it and adding that value to Arryalist and then
        //Using Adapter to make connection btwn data source and RV

        rv_audiolist = findViewById(R.id.rv_audiolist);
        userPostsAudiodetails = new ArrayList<>();

        //Setting layout manager and other things for our Recycler View
        rv_audiolist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_audiolist.setHasFixedSize(false);


        ad = new UserPostAudioListAdapter(userPostsAudiodetails, new UserPostAudioListAdapter.AudioitemClickListener() {
            @Override
            public void onitemclick(UserPostsAudiodetails userPostsAudiodetails) {
                pd.setMessage("Playing Audio...");
                pd.show();
                audioplaypage(userPostsAudiodetails.audio_name,userPostsAudiodetails.audio_ID,userPostsAudiodetails.audio_image,
                        userPostsAudiodetails.audio_file);
            }
        });

        rv_audiolist.setAdapter(ad);

        //GETTING DATA FROM FIREBASE AND ADDING IN OBJECT OF MODEL CLASS
        //ADDING THAT MODEL CLASS IN ARRAY LIST AND ADDING THAT IN ADAPTER CONSTRUCTOR
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Libraries").child(library_ID)
                .child("Audio");
        reference.orderByChild("audio_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userPostsAudiodetails.clear();
                //GETTING DATA FROM FIREBASE DATABASE AS A DATA SNAPSHOT OBJECT
                for (DataSnapshot ds:snapshot.getChildren()){
                    userPostsAudiodetailsobj = ds.getValue(UserPostsAudiodetails.class);

                    //ADDING DATA IN ARRAYLIST
                    userPostsAudiodetails.add(userPostsAudiodetailsobj);

                }

                ad.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        //Back Button to Home/Library Page
        BackToHome = findViewById(R.id.BackToHome);
        BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //THIS FUNCTION WHEN ITEM OF RV IS CLICKED IS CALLED
    //ONCLICK LIBRARY ITEM GO TO AUDIO PAGE
    private void audioplaypage(String audio_name,String audio_ID,String audio_image,String audio_file) {
        Intent i = new Intent(getApplicationContext(), AudioPlayPageActivity.class);

        i.putExtra("AudioList.audName",audio_name);
        i.putExtra("AudioList.audID",audio_ID);
        i.putExtra("AudioList.audImage",audio_image);
        i.putExtra("AudioList.audFile",audio_file);

        startActivity(i);
//        overridePendingTransition(0,0);
    }



    @Override
    protected void onStop() {
        super.onStop();

        pd.dismiss();


    }

}