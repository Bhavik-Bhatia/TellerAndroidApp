package com.example.teller;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAudioListAdapter extends RecyclerView.Adapter<MyAudioListAdapter.MyViewHolder> {

List<Audiodetails> audiodetails;
AudioitemClickListener itemclick;

    public MyAudioListAdapter(List<Audiodetails> audiodetails, AudioitemClickListener itemclick) {
        this.audiodetails = audiodetails;
        this.itemclick = itemclick;
    }
    public interface AudioitemClickListener {

        public void onitemclick(Audiodetails audiodetails);

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.Audioname.setText(audiodetails.get(position).audio_name);
        Picasso.get().load(audiodetails.get(position).audio_image).into(holder.Audioimageview);
        holder.itemView.setOnClickListener(v -> {

            itemclick.onitemclick(audiodetails.get(position));

        });
    }

    @Override
    public int getItemCount() {

        return audiodetails.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener  {
        ImageView Audioimageview;
        ImageView Audio_options_menu;
        TextView Audioname;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            Audioimageview = itemView.findViewById(R.id.Audioimageview);
            Audioname = itemView.findViewById(R.id.Audioname);
            Audio_options_menu = itemView.findViewById(R.id.Audio_options_menu);
            Audio_options_menu.setOnCreateContextMenuListener(this);
            Audio_options_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(),"Press and Hold Menu Icon",Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),1,0,"Remove Audio");
            menu.add(this.getAdapterPosition(),2,1,"Update Audio");

        }
    }
}
