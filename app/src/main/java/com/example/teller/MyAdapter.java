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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


    List<Librarydetails> librarydetails;
    itemClickListener itemclick;
    FirebaseAuth mAuth;

    //Constructor
    public MyAdapter(List<Librarydetails> librarydetails, itemClickListener itemclick) {
        this.librarydetails = librarydetails;
        this.itemclick = itemclick;
    }


    public interface itemClickListener {

        public void onitemclick(Librarydetails librarydetails);

    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.library_name.setText(librarydetails.get(position).getLibrary_name());

        //GETTING USERNAME OF CURRENT USER
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");


        //TO GET DATA OF JUST CURRENT USER
        Query query = reference.orderByChild("ID").equalTo(mAuth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String Username = ""+dataSnapshot.child("fullname").getValue();
                    holder.creator_name.setText(Username);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.itemView.setOnClickListener(v -> {

            itemclick.onitemclick(librarydetails.get(position));

        }

        );
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public int getItemCount() {
        return librarydetails.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView library_name;
        TextView creator_name;
        ImageView Library_options_menu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            library_name = itemView.findViewById(R.id.libraryname);
            creator_name = itemView.findViewById(R.id.creatorname);
            Library_options_menu = itemView.findViewById(R.id.Library_options_menu);
            Library_options_menu.setOnCreateContextMenuListener(this);
            Library_options_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(),"Press and Hold Menu Icon",Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(),1,0,"Remove Library");
            menu.add(this.getAdapterPosition(),2,1,"Update Library");
        }
    }
}
