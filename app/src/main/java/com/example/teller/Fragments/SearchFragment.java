package com.example.teller.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.teller.R;
import com.example.teller.Search_Users.Users;
import com.example.teller.Search_Users.UsersAdapter;
import com.example.teller.SearchedUserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    RecyclerView Userlist_rv;
    List<Users> usersList;
    SearchView searchView_users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_search, container, false);
        /*        RV with Card View
        Creating librarydetails object and setting values using it and adding that value to Arryalist and then
        Using Adapter to make connection btwn data source and RV
*/
        Userlist_rv = view.findViewById(R.id.Userlist_rv);
        usersList = new ArrayList<>();

        //setting layout manager
        Userlist_rv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        Userlist_rv.setHasFixedSize(false);

        //Search View
        searchView_users = view.findViewById(R.id.searchView_users);


        //Creating obj for Lib details class and adding the object data into Arraylist

        searchView_users.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.equals("")){
                    getSearchedusers(query);
                    Userlist_rv.setAlpha(1);
                }
                if(query.equals("")){
                    Userlist_rv.setAlpha(0);
                    Toast.makeText(getActivity().getApplicationContext(),"Not Found",Toast.LENGTH_SHORT).show();
                }

                return true;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!newText.equals("")){
                    getSearchedusers(newText);
                    Userlist_rv.setAlpha(1);
                }
                if(newText.equals("")){
                    Userlist_rv.setAlpha(0);
                }

                return true;
            }
        });


        return view;
    }
    public void getSearchedusers(String query){

        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        ///RUNS WHEN EVER CHANGE IN DATABASE
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                //GETTING ALL THE DATA IN dataSnapshpt Object and Adding it in USERS CLASS OBJ
                for (DataSnapshot ds:snapshot.getChildren()){
                    Users users = ds.getValue(Users.class);

                    //WILL NOT DISPLAY CURRENT USER IN LIST
                    if(!mAuth.getUid().equals(users.getID())){

                        //TO CHECK ONLY THOSE USERS WHICH SATISFY THE QUERY
                        if(users.getFullname().toLowerCase().startsWith(query.toLowerCase())) {
                            //ADDING USERS OBJ IN ARRAYLIST
                            usersList.add(users);
                        }
                    }
                    //ADDING ARRAYLIST IN ADAPTER CONST AND CONNECTION RV WITH DATA SOURCE
                    UsersAdapter usersAdapter = new UsersAdapter(usersList, new UsersAdapter.ItemclickListener() {
                        @Override
                        public void onitemclick(Users users) {
                            //This users OBJECT will contain the Details of the ITEM CLICKED ON
                            //PASS THIS DETAILS TO A FUNCTION WHICH WILL OPEN INTENT TO NEW ACTIVITY

                                openSearchUserActivity(users);
                        }
                    });
                    //Refresh Adapter

                    usersAdapter.notifyDataSetChanged();
                    Userlist_rv.setAdapter(usersAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openSearchUserActivity(Users obj){

          Intent i = new Intent(getContext(),SearchedUserProfile.class);

          i.putExtra("com.example.imusic.Fragments.SearchFragment.ID",obj.getID());
          i.putExtra("com.example.imusic.Fragments.SearchFragment.fullname",obj.getFullname());
          i.putExtra("com.example.imusic.Fragments.SearchFragment.Bio",obj.getBio());
          i.putExtra("com.example.imusic.Fragments.SearchFragment.email",obj.getEmail());
          i.putExtra("com.example.imusic.Fragments.SearchFragment.profile_img",obj.getProfile_img());

          startActivity(i);

    }

}