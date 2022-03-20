 package com.example.teller.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
//import android.icu.text.DecimalFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teller.HomePage;
import com.example.teller.R;
import com.example.teller.UserPosts.ModalUserPosts;
import com.example.teller.UserPosts.UserPostsAdapter;
import com.example.teller.UserPostsAudioListPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.text.DecimalFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    ImageView EditProfileOptions, Profile_User_img;
    TextView Profile_UN, Profile_UserBio,FollowersNumber,FollowingNumber;
    //CREATING AUTH REF AND USER REF AND DATABASE REF
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    ProgressDialog pd;

    ///CREATING A STORAGE REFERENCE
    StorageReference storageReference;
    String storage_Path = "Users_Profile_Img/";


    private final int PICK_IMAGE = 1;
    Uri ImageUri;

    String full_name,bio;


    private int count_followers;
    private int count_following;

    List<ModalUserPosts> modalUserPosts;
    UserPostsAdapter ad;
    ModalUserPosts modalUserPostsobj;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        FollowersNumber = view.findViewById(R.id.FollowersNumber);
        FollowingNumber = view.findViewById(R.id.FollowingNumber);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


                /*        RV with Card View
        Creating librarydetails object and setting values using it and adding that value to Arryalist and then
        Using Adapter to make connection btwn data source and RV
*/
        RecyclerView rv = view.findViewById(R.id.rv);

        //setting layout manager
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
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

                    if(user.getUid().equals(modalUserPostsobj.getUID())){
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
        FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
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

        FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getUid())
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




        ///PROGRESS DIALOG INIT
        pd = new ProgressDialog(getContext());

        /// Intializing Firebase Variables

        mAuth = mAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        //Init Views
        Profile_User_img = view.findViewById(R.id.Profile_User_img);
        Profile_UN = view.findViewById(R.id.Profile_UN);
        Profile_UserBio = view.findViewById(R.id.Profile_UserBio);

        //QUERY TO GET ONLY CURRENT USERS DATA FROM USERS CHILD
        Query query = databaseReference.orderByChild("ID").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //GET DATA
                    full_name = "" + ds.child("fullname").getValue();
                    String email = "" + ds.child("email").getValue();
                    bio = "" + ds.child("bio").getValue();
                    String image_url = "" + ds.child("profile_img").getValue();

                    Profile_UN.setText(full_name);
                    Profile_UserBio.setText(bio);
                    Picasso.get().load(image_url).into(Profile_User_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //ON EDIT OPTION CLICKED OPENING ALERT DIALOG BOXES
        EditProfileOptions = view.findViewById(R.id.EditProfileOptions);
        EditProfileOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowProfileEditOptions();
            }
        });


        return view;
    }

    public void audiopage(String Library_ID,String Library_name){

        Intent i = new Intent(getActivity(), UserPostsAudioListPage.class);
        i.putExtra("com.example.imusic.NotificationFragment.lib_id",Library_ID);
        i.putExtra("com.example.imusic.NotificationFragment.lib_name",Library_name);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);


    }


    //OPEN A ALERT DIALOG BOX WITH OPTIONS FOR EDITING
    public void ShowProfileEditOptions() {

        String options[] = {"Edit Profile Image", "Edit Username", "Edit Bio", "Edit Password"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    profile_img_input();
                } else if (which == 1) {
                    profile_UN_input();
                } else if (which == 2) {
                    profile_Bio_input();
                } else if (which == 3) {
                    forgot_password_update();
                }
            }
        });

        builder.create().show();

    }


    //
    public void profile_UN_input(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Edit Username");

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        EditText editText = new EditText(getActivity());

        editText.setText(full_name);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20) });

        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String input_username = editText.getText().toString();

                if (input_username.equals("")){
                    Toast.makeText(getActivity(),"Add Username to edit",Toast.LENGTH_SHORT).show();
                }else {
                    pd = new ProgressDialog(getActivity());
                    pd.setMessage("Updating UserName....");
                    pd.show();

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("fullname",input_username);

                    FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Updated Username",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Username Not Updated",Toast.LENGTH_SHORT).show();
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
    }


    public void profile_Bio_input(){


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Edit Bio");

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        EditText editText = new EditText(getActivity());

        editText.setText(bio);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40) });

        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String input_bio = editText.getText().toString();

                if (input_bio.equals("")){
                    Toast.makeText(getActivity(),"Add Bio to edit",Toast.LENGTH_SHORT).show();
                }else {
                    pd = new ProgressDialog(getActivity());
                    pd.setMessage("Updating Bio....");
                    pd.show();

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("bio",input_bio);

                    FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),"Updated Bio",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Bio Not Updated",Toast.LENGTH_SHORT).show();
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

    }

    //RUN FOR GETTING USER IMAGE INPUT
    public void profile_img_input() {
//        Intent gallery = new Intent();
//        gallery.setType("image/*");
//        gallery.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);

        ///CROPPING  IMAGE IN FRAGMENT
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).
                setAspectRatio(1,1).start(getContext(),this);
    }


    //GOT USER IMAGE ->TO STORAGE -> GETTING URL FROM STORAGE ->ADDED TO FIREBASE DB
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == getActivity().RESULT_OK && null != data) {
//            ImageUri = data.getData(); ///Image is Picked

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

                //OUR IMAGE IS HERE IN THIS URI

                Uri resUri = result.getUri();

                //CONVERTING TO FILE
                File file = new File(resUri.getPath());
                Long fileSizeInBytes = file.length();

                DecimalFormat df = new DecimalFormat("0.00");

                float sizeKb = 1024.0f;
                float sizeMb = sizeKb * sizeKb;
                float sizeGb = sizeMb * sizeKb;

                //IF SIZE IS IN MB
                if(fileSizeInBytes < sizeGb) {
                    float size = Float.parseFloat(df.format(fileSizeInBytes / sizeMb));

                    //IF SIZE IS GREATER THAN 5 MB IT IS NOT ACCEPTED

                    if (size >= 2){
                        Toast.makeText(getActivity(),"Size is "+df.format(fileSizeInBytes / sizeMb)+" MB"
                                        +",should be less than 2 MB"
                                ,Toast.LENGTH_LONG).show();
                    }else{
                        pd.setMessage("Profile Image being updated....");
                        pd.show();

                        String filePath_Id = storage_Path+"Image"+"_"+user.getUid();
                        StorageReference storageReference1 = storageReference.child(filePath_Id);
                        storageReference1.putFile(resUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                //GET URL FROM STORAGE
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());
                                Uri downloadUri = uriTask.getResult();

                                if (uriTask.isSuccessful()){

                                    //Add the PROFILE IMAGE URL TO REALTIME DATABASE
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("profile_img",downloadUri.toString());

                                    databaseReference.child(user.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            ///WHEN CHILD CURRENT USERID IS UPDATED WITH HASHMAP OBJECT SUCCESSFULLY
                                            pd.dismiss();
                                            Toast.makeText(getContext(),"Image Update..",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(),"Error Updating Image..",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else{
                                    ///IF WE DONT GET URL FROM STORAGE
                                    pd.dismiss();
                                    Toast.makeText(getContext(),"Some error occured..",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(getContext(),""+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

        }
    }

    //ONCLICK EMAIL SENT TO REGISTERED EMAIL ADDRESS
    public void forgot_password_update(){
        //Forgot Password on registered Email
        pd.setMessage("Sending email to registered email address.....");
        pd.show();
        mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(getContext(),"Email sent",Toast.LENGTH_LONG).show();
                }else{
                    pd.dismiss();
                    Toast.makeText(getContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}

