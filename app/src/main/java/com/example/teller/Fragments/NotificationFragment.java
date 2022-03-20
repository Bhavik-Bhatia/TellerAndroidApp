package com.example.teller.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.teller.R;
import com.example.teller.UserPosts.ModalUserPosts;
import com.example.teller.UserPosts.UserPostsAdapter;
import com.example.teller.UserPostsAudioListPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    RecyclerView rv_user_posts;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    List<ModalUserPosts> modalUserPosts;
    UserPostsAdapter ad;
    ModalUserPosts modalUserPostsobj;

    private List<String> followingList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        //INIT AUTHENTICATION AND USER
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        followingList = new ArrayList<>();
        checkFollowing(firebaseUser);

/*        RV with Card View
        Creating librarydetails object and setting values using it and adding that value to Arryalist and then
        Using Adapter to make connection btwn data source and RV
*/
        rv_user_posts = v.findViewById(R.id.rv_user_posts);

        //setting layout manager
        rv_user_posts.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rv_user_posts.setHasFixedSize(false);

        modalUserPosts = new ArrayList<>();

        //Calling adapter constructor which takes in Arraylist and we connect rv with adpater
        ad = new UserPostsAdapter(modalUserPosts, new UserPostsAdapter.itemClickListener() {
            @Override
            public void onitemclick(ModalUserPosts modalUserPosts) {
                audiopage(modalUserPosts.getLIB_ID(),modalUserPosts.getLibrary_name());
            }
        });

        rv_user_posts.setAdapter(ad);

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

                        for(String id : followingList){
                            //ARRAYLIST OF IDS OF USERS YOU ARE FOLLOWING
                            //THEN WE EQUAL IT WITH USER ID OF LIBRRAY OWNER
                            //THEN WE GET DATA ONLY OF LIBRARIES WHOSE USER WE FOLLOW
                            if(modalUserPostsobj.getUID().equals(id)){
                                //ADDING DATA IN ARRAYLIST
                                modalUserPosts.add(modalUserPostsobj);
                            }
                        }

                }
                ad.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        return v;
    }


    //FUNCTION TO GET LIST OF USERS WE ARE FOLLOWING

    public void checkFollowing(FirebaseUser user){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").
                child(user.getUid()).child("Following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot ds :snapshot.getChildren()){
                    followingList.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void audiopage(String Library_ID,String Library_name){

        Intent i = new Intent(getActivity(), UserPostsAudioListPage.class);
        i.putExtra("com.example.imusic.NotificationFragment.lib_id",Library_ID);
        i.putExtra("com.example.imusic.NotificationFragment.lib_name",Library_name);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);


    }

}