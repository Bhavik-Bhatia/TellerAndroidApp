
package com.example.teller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class AudioListPage extends AppCompatActivity {


    ImageView BackToHome,fab;
    TextView Librarygetname;
    List<Audiodetails> audiodetails;
    Audiodetails audiodetailsobj;
    RecyclerView rv_audiolist;
    CardView Audiocardview;
    MyAudioListAdapter ad;
    FirebaseAuth mAuth;

    ///CREATING A STORAGE REFERENCE
    StorageReference storageReference;
    String storage_Path = "Audio_List_files/";

    ProgressDialog pd;

    String library_ID;
    String Library_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_audio_list_page);

        pd = new ProgressDialog(AudioListPage.this);



        //GETTING DATA FROM LIBRARY FRAGMENT
        Intent i = getIntent();

        Librarygetname = findViewById(R.id.Librarygetname);

        ///GETTING CURRENT USER
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();


        //INIT PROFRESS DIALOG
        pd = new ProgressDialog(this);

        SharedPreferences sh = getSharedPreferences("MySharedPref",0);



        if(i.getStringExtra("com.example.imusic.LibraryFragment.lib_name") != null){

            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            myEdit.putString("library_name",i.getStringExtra("com.example.imusic.LibraryFragment.lib_name"));
            myEdit.putString("library_id",i.getStringExtra("com.example.imusic.LibraryFragment.lib_id"));
            myEdit.commit();
            myEdit.apply();

        }
        if(sh != null){
            Library_name = sh.getString("library_name", "");
            library_ID = sh.getString("library_id", "");

            Librarygetname.setText(Library_name);
        }

        if (i.getStringExtra("com.example.imusic.Custom_AudioInput_Activity.AudioName") != null){

            //GETTING DATA FROM CUSTOM INPUT AUDIO ACTIVITY
            String audio_name = i.getStringExtra("com.example.imusic.Custom_AudioInput_Activity.AudioName");
            String img_uri_str  = i.getStringExtra("com.example.imusic.Custom_AudioInput_Activity.AudioImage");
            String aud_uri_str  = i.getStringExtra("com.example.imusic.Custom_AudioInput_Activity.AudioFile");

            //PARSE STRING TO URI
            Uri img_uri = Uri.parse(img_uri_str);
            Uri aud_uri = Uri.parse(aud_uri_str);


                ///GETTING PROGRESS DIALOG TO WORK
                pd.setMessage("Adding Audio...");
                pd.show();

                //GETTING A UNIQUE ID FOR FIREBASE STORAGE AND REALTIME DATABASE
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Libraries").child("Audio");

                //CREATING A UNIQUE ID FOR AUDIO
                String Audio_ID = UUID.randomUUID().toString()+"_"+reference.push().getKey();

                //NOW WE ADD THIS URI (IMAGE) INTO FIREBASE STORAGE

                String filePath_Id = storage_Path+"User_"+firebaseUser.getUid()+"/"+"Library_"+library_ID+"/"+"Audio_"+Audio_ID+"/"+Audio_ID+"_"+"Image";
                storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference storageReference1 = storageReference.child(filePath_Id);
                storageReference1.putFile(img_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //GET URL FROM STORAGE
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());

                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()){
                              Add_audio_details(audio_name, downloadUri, aud_uri, Audio_ID, library_ID);
                        }else{
                            ///IF WE DONT GET URL FROM STORAGE
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Some error occured..",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),""+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
            }//END OF IF





        //RV with Card View
        //Creating librarydetails object and setting values using it and adding that value to Arryalist and then
        //Using Adapter to make connection btwn data source and RV

        rv_audiolist = findViewById(R.id.rv_audiolist);
        audiodetails = new ArrayList<>();

        //Setting layout manager and other things for our Recycler View
        rv_audiolist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_audiolist.setHasFixedSize(false);


        //Calling adapter constructor which takes in Arraylist and we connect rv with adpater
        ad = new MyAudioListAdapter(audiodetails, new MyAudioListAdapter.AudioitemClickListener() {
            @Override
            public void onitemclick(Audiodetails audiodetails) {
                pd.setMessage("Playing Audio...");
                pd.show();
                audioplaypage(audiodetails.getAudio_name(),audiodetails.getAudio_ID(),audiodetails.getAudio_image(),audiodetails.getAudio_file());

            }
        });


        rv_audiolist.setAdapter(ad);


        //GETTING DATA FROM FIREBASE AND ADDING IN OBJECT OF MODEL CLASS
        //ADDING THAT MODEL CLASS IN ARRAY LIST AND ADDING THAT IN ADAPTER CONSTRUCTOR
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Libraries").child(library_ID)
                .child("Audio");
        reference.orderByChild("audio_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                audiodetails.clear();
                //GETTING DATA FROM FIREBASE DATABASE AS A DATA SNAPSHOT OBJECT
                for (DataSnapshot ds:snapshot.getChildren()){
                    audiodetailsobj = ds.getValue(Audiodetails.class);

                        //ADDING DATA IN ARRAYLIST
                        audiodetails.add(audiodetailsobj);

                }

                ad.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //Back Button to Home/Library Page
        BackToHome = findViewById(R.id.BackToHome);
        BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //While user clicks on Floating action button to open the dialog box
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Custom_AudioInput_Activity.class));
                finish();
            }
        });



    }//ONCREATE ENDS HERE


    //THIS FUNCTION WHEN ITEM OF RV IS CLICKED IS CALLED
    //ONCLICK LIBRARY ITEM GO TO AUDIO PAGE
    private void audioplaypage(String audio_name,String audio_ID,String audio_image,String audio_file) {
        Intent i = new Intent(getApplicationContext(), AudioPlayPageActivity.class);

        i.putExtra("AudioList.audName",audio_name);
        i.putExtra("AudioList.audID",audio_ID);
        i.putExtra("AudioList.audImage",audio_image);
        i.putExtra("AudioList.audFile",audio_file);

        startActivity(i);
//        overridePendingTransition(0,0);
    }


    ///WILL RUN AFTER AUDIO IMAGE IS ADDED INTO FIREBASE STORAGE ---- >
    // HERE WE ADD AUDIO FILE IN STORAGE AND ADD NAME IMAGE FILE IN FIRE BASE DATABASE
    public void Add_audio_details(String Audio_name, Uri download_audio_image_uri, Uri aud_uri,String Audio_ID,String Library_ID){

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String filePath_Id_audio = storage_Path+"User_"+firebaseUser.getUid()+"/"+"Library_"+Library_ID+"/"+"Audio_"+Audio_ID+"/"+Audio_ID+"_"+"Audio";

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference1 = storageReference.child(filePath_Id_audio);

        storageReference1.putFile(aud_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()

        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //GET URL FROM STORAGE
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                Uri download_audio_file_uri = uriTask.getResult();
                if (uriTask.isSuccessful()){

//                  HERE WE ADD NAME FILE AND IMAGE IN FIREBASE DATABASE

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("audio_name",Audio_name);
                    hashMap.put("audio_image",download_audio_image_uri.toString());
                    hashMap.put("audio_file",download_audio_file_uri.toString());
                    hashMap.put("audio_ID",Audio_ID);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Libraries").child(Library_ID).child("Audio").child(Audio_ID);
                    databaseReference.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Successfully Added",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    ///IF WE DONT GET URL FROM STORAGE
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Some error occured..",Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),""+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

        @Override
        public boolean onContextItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case 1:
                    ///DELETING THE DATA FROM FIREBASE AND NOtIFY ADAPTER
                    pd.setMessage("Deleting Audio....");
                    pd.show();

                    ///DELETING THE DATA FROM FIREBASE AND NOtIFY ADAPTER

                    FirebaseDatabase.getInstance().getReference("Libraries").child(library_ID).child("Audio")
                            .child(audiodetails.get(item.getGroupId()).getAudio_ID()).removeValue();
                    ad.notifyDataSetChanged();
                    pd.dismiss();

                    return true;

                case 2:
                    ///HERE WE WILL OPEN UPDATE INPUT AUDIO ACTIVITY AND PASS AUDIO ID , LIBRARY ID , AUDIO NAME , AUDIO FILE ,
                    // AUDIO IMAGE

                    Intent i = new Intent(getApplicationContext(),Update_AudioInput_Activity.class);
                    i.putExtra("AudioList.audName",audiodetails.get(item.getGroupId()).getAudio_name());
                    i.putExtra("AudioList.audID",audiodetails.get(item.getGroupId()).getAudio_ID());
                    i.putExtra("AudioList.audImage",audiodetails.get(item.getGroupId()).getAudio_image());
                    i.putExtra("AudioList.audFile",audiodetails.get(item.getGroupId()).getAudio_file());
                    i.putExtra("AudioList.LibID",library_ID);
                    startActivity(i);

                    return true;

                default:
                    return super.onContextItemSelected(item);

            }
        }

    @Override
    protected void onStop() {
        super.onStop();

        pd.dismiss();


    }
}

