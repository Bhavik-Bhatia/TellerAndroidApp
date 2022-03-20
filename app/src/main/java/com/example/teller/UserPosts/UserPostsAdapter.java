package com.example.teller.UserPosts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teller.Librarydetails;
import com.example.teller.MyAdapter;
import com.example.teller.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserPostsAdapter extends RecyclerView.Adapter<UserPostsAdapter.MyViewHolder> {

    List<ModalUserPosts> modalUserPosts;
    FirebaseAuth mAuth;
    UserPostsAdapter.itemClickListener itemclick;

    public UserPostsAdapter(List<ModalUserPosts> modalUserPosts, itemClickListener itemclick) {
        this.modalUserPosts = modalUserPosts;
        this.itemclick = itemclick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_posts_library_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    public interface itemClickListener {

        public void onitemclick(ModalUserPosts modalUserPosts);

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //SETTING LIBRARY NAME
        holder.library_name.setText(modalUserPosts.get(position).Library_name);

        //GETTING USER ID TO GET USERNAME
        String user_id = modalUserPosts.get(position).UID;
//        //GETTING USERNAME OF THAT USER ID AND SETTING IT ONTO USERNAME TEXTVIEW

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByChild("ID").equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //GETTING DATA FROM FIREBASE DATABASE AS A DATA SNAPSHOT OBJECT
                for (DataSnapshot ds:snapshot.getChildren()){
                    String username = ""+ds.child("fullname").getValue();
                    holder.creator_name.setText(username);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        holder.itemView.setOnClickListener(v -> {

            itemclick.onitemclick(modalUserPosts.get(position));

        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //CHECKING FOR LIKE OR LIKED
        checkLike(modalUserPosts.get(position).LIB_ID,holder.LikeButton);
        countLikes(modalUserPosts.get(position).LIB_ID,holder.numOfLikes);

        holder.LikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.LikeButton.getText().toString().equals("Like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").
                            child(modalUserPosts.get(position).LIB_ID).child(user.getUid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Likes").
                            child(modalUserPosts.get(position).LIB_ID).child(user.getUid()).removeValue();

                }
            }
        });
    }

    public void checkLike(String Library_ID,Button LikeButton){

        //TO CHECK IF USER HAS ALREADY LIKED OR NOT
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes"
        ).child(Library_ID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(user.getUid()).exists()){
                    LikeButton.setText("Liked");
                    LikeButton.setBackgroundColor(Color.GREEN);

                }else {
                    LikeButton.setText("Like");
                    LikeButton.setBackgroundColor(Color.parseColor("#2196F3"));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void countLikes(String Library_ID,TextView numOfLikes){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes"
        ).child(Library_ID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numOfLikes.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return modalUserPosts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView library_name,creator_name,numOfLikes;
        Button LikeButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            library_name = itemView.findViewById(R.id.libraryname);
            creator_name = itemView.findViewById(R.id.creatorname);
            numOfLikes = itemView.findViewById(R.id.numOfLikes);
            LikeButton = itemView.findViewById(R.id.LikeButton);

        }
    }
}
