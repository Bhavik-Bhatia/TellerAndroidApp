package com.example.teller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UserPostAudioListAdapter extends RecyclerView.Adapter<UserPostAudioListAdapter.MyViewHolder> {


    List<UserPostsAudiodetails> userPostsAudiodetails;
    UserPostAudioListAdapter.AudioitemClickListener itemclick;

    public UserPostAudioListAdapter(List<UserPostsAudiodetails> userPostsAudiodetails, AudioitemClickListener itemclick) {
        this.userPostsAudiodetails = userPostsAudiodetails;
        this.itemclick = itemclick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_audio_item,parent,false);

        UserPostAudioListAdapter.MyViewHolder myViewHolder = new UserPostAudioListAdapter.MyViewHolder(view);

        return myViewHolder;
    }

    public interface AudioitemClickListener {

        public void onitemclick(UserPostsAudiodetails userPostsAudiodetails);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Audioname.setText(userPostsAudiodetails.get(position).audio_name);
        Picasso.get().load(userPostsAudiodetails.get(position).audio_image).into(holder.Audioimageview);
        holder.itemView.setOnClickListener(v -> {

            itemclick.onitemclick(userPostsAudiodetails.get(position));

        });

    }

    @Override
    public int getItemCount() {
        return userPostsAudiodetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView Audioimageview;
        TextView Audioname;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Audioimageview = itemView.findViewById(R.id.Audioimageview);
            Audioname = itemView.findViewById(R.id.Audioname);

        }
    }
}
