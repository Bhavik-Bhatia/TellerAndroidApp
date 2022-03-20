package com.example.teller.Search_Users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teller.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    List<Users> usersList;
    ItemclickListener itemclickListener;


    public UsersAdapter(List<Users> usersList, ItemclickListener itemclickListener) {
        this.usersList = usersList;
        this.itemclickListener = itemclickListener;
    }

    public interface ItemclickListener{
        public void onitemclick(Users users);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searched_userlist_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.Searhed_username.setText(usersList.get(position).fullname);
        Picasso.get().load(usersList.get(position).profile_img).into(holder.UserListimageview);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemclickListener.onitemclick(usersList.get(position));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView UserListimageview;
        TextView Searhed_username;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ///      INITIALIZING VALUES
            UserListimageview = itemView.findViewById(R.id.UserListimageview);
            Searhed_username = itemView.findViewById(R.id.Searhed_username);

        }
    }
}
