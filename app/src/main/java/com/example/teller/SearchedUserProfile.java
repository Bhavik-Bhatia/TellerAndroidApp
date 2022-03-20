package com.example.teller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teller.UserPosts.ModalUserPosts;
import com.example.teller.UserPosts.UserPostsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchedUserProfile extends AppCompatActivity {

    TextView Searhed_UN,Searhed_UserBio,FollowersNumber,FollowingNumber;
    ImageView Searhed_User_img,BackToSearchPage;
    Button FollowButton;
    FirebaseUser user;

    private int count_followers;
    private int count_following;

    List<ModalUserPosts> modalUserPosts;
    UserPostsAdapter ad;
    ModalUserPosts modalUserPostsobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_searched_user_profile);

        Intent i = getIntent();

        //GETTING VALUES FROM ONCLICKED USER
        String Id = i.getStringExtra("com.example.imusic.Fragments.SearchFragment.ID");
        String full_name = i.getStringExtra("com.example.imusic.Fragments.SearchFragment.fullname");
        String profile_img = i.getStringExtra("com.example.imusic.Fragments.SearchFragment.profile_img");
        String Bio = i.getStringExtra("com.example.imusic.Fragments.SearchFragment.Bio");
        String email = i.getStringExtra("com.example.imusic.Fragments.SearchFragment.email");

        ///INIT LAYOUT COMPONENTS
        Searhed_UN = findViewById(R.id.Searhed_UN);
        Searhed_UserBio = findViewById(R.id.Searhed_UserBio);
        Searhed_User_img = findViewById(R.id.Searhed_User_img);
        BackToSearchPage = findViewById(R.id.BackToSearchPage);
        FollowButton = findViewById(R.id.FollowButton);
        FollowersNumber = findViewById(R.id.FollowersNumber);
        FollowingNumber = findViewById(R.id.FollowingNumber);


        //SETTING UP RECYCLER VIEW OF OUR POSTS
        //INIT AUTHENTICATION AND USER
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /*        RV with Card View
        Creating librarydetails object and setting values using it and adding that value to Arryalist and then
        Using Adapter to make connection btwn data source and RV
*/
        RecyclerView rv = findViewById(R.id.rv);

        //setting layout manager
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setHasFixedSize(false);

        modalUserPosts = new ArrayList<>();

        //Calling adapter constructor which takes in Arraylist and we connect rv with adpater
        ad = new UserPostsAdapter(modalUserPosts, new UserPostsAdapter.itemClickListener() {
            @Override
            public void onitemclick(ModalUserPosts modalUserPosts) {
                audiopage(modalUserPosts.getLIB_ID(),modalUserPosts.getLibrary_name());
            }
        });

        rv.setAdapter(ad);


        //GETTING DATA FROM FIREBASE AND ADDING IN OBJECT OF MODEL CLASS
        //ADDING THAT MODEL CLASS IN ARRAY LIST AND ADDING THAT IN ADAPTER CONSTRUCTOR
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Libraries");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modalUserPosts.clear();
                //GETTING DATA FROM FIREBASE DATABASE AS A DATA SNAPSHOT OBJECT
                for (DataSnapshot ds:snapshot.getChildren()){
                    modalUserPostsobj = ds.getValue(ModalUserPosts.class);

                        if(Id.equals(modalUserPostsobj.getUID())){
                            //ADDING DATA IN ARRAYLIST
                            modalUserPosts.add(modalUserPostsobj);
                        }
                }

                ad.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        //SETTING UP FOLLOWERS AND FOLLOWING COUNT
        //NUMBER OF FOLLOWERS
        FirebaseDatabase.getInstance().getReference().child("Follow").child(Id)
                .child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ///GETTING COUNT OF USER IDS IN FOLLOW > USER PROFILE ID > FOLLOWERS >....

                if(snapshot.exists()){
                    count_followers = (int) snapshot.getChildrenCount();
                    FollowersNumber.setText(Integer.toString(count_followers));

                }else{
                    FollowersNumber.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Follow").child(Id)
                .child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ///GETTING COUNT OF USER IDS IN FOLLOW > USER PROFILE ID > FOLLOWERS >....

                if(snapshot.exists()){
                    count_following = (int) snapshot.getChildrenCount();
                    FollowingNumber.setText(Integer.toString(count_following));

                }else{
                    FollowingNumber.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //RUNNING THE IS FOLLOWING METHOD
        isFollowing(Id,FollowButton);

        ///SETTING VALUES IN LAYOUT COMPONENT
        Picasso.get().load(profile_img).into(Searhed_User_img);
        Searhed_UN.setText(full_name);
        Searhed_UserBio.setText(Bio);

        BackToSearchPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //WHEN ANY USER CLICKS ON THE FOLLOW BUTTON DATA IS ADDED AND WHEN HE CLICKS AGAIN DATA IS DELETED
        FollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FollowButton.getText().toString().equals("Follow")){
                    //THIS SHOWS WHEN BUTTON IS FOLLOW USER CLICKS
                    //FOLLOW > CURRENT USER > IS FOLLOWING > USER OF PROFILE
                    //FOLLOW > USER OF PROFILE > HAS FOLLOWERS > CURRENT USER

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid()).child("Following")
                    .child(Id).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(Id).child("Followers")
                            .child(user.getUid()).setValue(true);
                    Toast.makeText(getApplicationContext(),"Following"+" "+full_name,Toast.LENGTH_SHORT).show();
                }else{
                    //THIS SHOWS WHEN BUTTON IS FOLLOWING USER CLICKS THE DATA IS REMOVED

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid()).child("Following")
                            .child(Id).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(Id).child("Followers")
                            .child(user.getUid()).removeValue();
                    Toast.makeText(getApplicationContext(),"Unfollowed"+" "+full_name,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void audiopage(String Library_ID,String Library_name){

        Intent i = new Intent(getApplicationContext(), UserPostsAudioListPage.class);
        i.putExtra("com.example.imusic.NotificationFragment.lib_id",Library_ID);
        i.putExtra("com.example.imusic.NotificationFragment.lib_name",Library_name);
        startActivity(i);
        overridePendingTransition(0, 0);


    }


    //TO CHECK WHEN PAGE OPENS IF THE BUTTON WILL SHOW FOLLOW OR FOLLOWING
    public void isFollowing(String user_ID,Button followButton){

        user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid()).
                child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(user_ID).exists()){
                    followButton.setText("Following");
                }else{
                    followButton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}