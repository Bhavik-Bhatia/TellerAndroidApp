package com.example.teller.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teller.AudioListPage;
import com.example.teller.Librarydetails;
import com.example.teller.MainActivity;
import com.example.teller.MyAdapter;
import com.example.teller.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LibraryFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    RecyclerView rv;
    List<Librarydetails> librarydetails;
    FloatingActionButton fab;
    TextView Signout;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    ImageView Library_options_menu;
    MyAdapter ad;
    Librarydetails librarydetailsobj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        //INIT AUTHENTICATION AND USER
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();


/*        RV with Card View
        Creating librarydetails object and setting values using it and adding that value to Arryalist and then
        Using Adapter to make connection btwn data source and RV
*/
        rv = view.findViewById(R.id.rv);
        librarydetails = new ArrayList<>();

        //setting layout manager
        rv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        rv.setHasFixedSize(false);

        //Calling adapter constructor which takes in Arraylist and we connect rv with adpater
        ad = new MyAdapter(librarydetails, new MyAdapter.itemClickListener() {
            @Override
            public void onitemclick(Librarydetails librarydetails) {

                audiopage(librarydetails.getLIB_ID(),librarydetails.getLibrary_name());
            }
        });
        rv.setAdapter(ad);


        //GETTING DATA FROM FIREBASE AND ADDING IN OBJECT OF MODEL CLASS
        //ADDING THAT MODEL CLASS IN ARRAY LIST AND ADDING THAT IN ADAPTER CONSTRUCTOR
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Libraries");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                librarydetails.clear();
                //GETTING DATA FROM FIREBASE DATABASE AS A DATA SNAPSHOT OBJECT
                for (DataSnapshot ds:snapshot.getChildren()){
                    librarydetailsobj = ds.getValue(Librarydetails.class);
                    //TO GET DATA JUST OF THE CURRENT USER
                    if(mAuth.getUid().equals(librarydetailsobj.getUID())){

                        //ADDING DATA IN ARRAYLIST
                        librarydetails.add(librarydetailsobj);
                    }
                }
                ad.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        //On pressing SIGN OUT
        Signout = view.findViewById(R.id.Signout);
        Signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signout();
            }
        });

        //GET LIBRARY NAME STRING FROM HOME ACT TO THIS FRAGMENT
        Bundle b = getArguments();
        if (b != null){
            pd = new ProgressDialog(getContext());
            pd.setMessage("Adding Library...");
            pd.show();


            /// CURRENT USER_ID , LIBRARY NAME , LIBRARY ID
            String uid = firebaseUser.getUid();
            String library_name =  b.getString("input_lib_name_key");
            reference = FirebaseDatabase.getInstance().getReference().child("Libraries");

            //GETTING LIBRARY UNIQUE ID
            String Lib_ID = reference.push().getKey();

            HashMap<String,Object> hashMap = new HashMap<>();

            hashMap.put("UID",uid);
            hashMap.put("LIB_ID",Lib_ID);
            hashMap.put("Library_name",library_name);

            reference.child(Lib_ID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //When the values are successfully added in Users Collection
                    if(task.isSuccessful()){
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Library Added!", Toast.LENGTH_SHORT).show();
                    }else{
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Error: Library could not be made..", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        return view;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 1:

                pd = new ProgressDialog(getActivity());
                pd.setMessage("Deleting Library....");
                pd.show();

                ///DELETING THE DATA FROM FIREBASE AND NOtIFY ADAPTER
                FirebaseDatabase.getInstance().getReference("Libraries").child(librarydetails.get(item.getGroupId()).getLIB_ID()).removeValue();
                ad.notifyDataSetChanged();
                pd.dismiss();

                return true;

            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Update");

                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(10,10,10,10);

                EditText editText = new EditText(getActivity());

                editText.setText(librarydetails.get(item.getGroupId()).getLibrary_name());
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20) });

                linearLayout.addView(editText);
                builder.setView(linearLayout);

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //GETTING NEW LIBRARY NAME
                        String new_Library_name = editText.getText().toString();

                        if (new_Library_name.equals("")){
                            Toast.makeText(getActivity(),"Add library name",Toast.LENGTH_SHORT).show();
                        }else {

                            pd = new ProgressDialog(getActivity());
                            pd.setMessage("Updating Library Name....");
                            pd.show();

                            HashMap<String,Object> hashMap = new HashMap<>();
                            hashMap.put("Library_name",new_Library_name);

                            FirebaseDatabase.getInstance().getReference("Libraries").child(librarydetails.get(item.getGroupId()).getLIB_ID()).
                                    updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Updated",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Not Updated",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }


    //SIGNOUT METHOD WILL BE CALLED WHEN CLICKED ON SIGNOUT
    public void Signout(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
    }

    //ONCLICK LIBRARY ITEM GO TO AUDIO LIST FRAGMENT PAGE
    private void audiopage(String lib_id,String library_name) {

    //  TRANSFER ITEM LIBRARY NAME TO AUDIO LIST FRAGMENT

        Intent i = new Intent(getActivity(), AudioListPage.class);
        i.putExtra("com.example.imusic.LibraryFragment.lib_id",lib_id);
        i.putExtra("com.example.imusic.LibraryFragment.lib_name",library_name);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        return false;
    }
}
