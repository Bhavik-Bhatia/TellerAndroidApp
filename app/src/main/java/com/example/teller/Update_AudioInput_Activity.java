package com.example.teller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
//import android.icu.text.DecimalFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Update_AudioInput_Activity extends AppCompatActivity {
    private EditText input_audio_name;
    private CircleImageView input_audio_image,input_audio_image2;
    private TextView imgtexttitle,audiotexttitle,UploadTitle;
    private final int PICK_IMAGE = 1;
    private final int PICK_AUDIO = 1;
    private boolean choose_audio = false;
    private boolean choose_image = false;
    Uri ImageUri;
    Uri AudioUri;
    ImageView BackToHome;

    ProgressDialog pd;

    String Library_ID;
    String Audio_ID;

    FirebaseAuth mAuth;
    StorageReference storageReference;
    String storage_Path = "Audio_List_files/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_update__audio_input_);

        //Back Button to Home/Library Page
        BackToHome = findViewById(R.id.BackToHome);
        BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        //Finding the XML LAYOUT ELEMENTS....
        input_audio_name = findViewById(R.id.input_audio_name);
        input_audio_image= findViewById(R.id.input_audio_image);
        input_audio_image2= findViewById(R.id.input_audio_image2);
        imgtexttitle = findViewById(R.id.imgtexttitle);
        audiotexttitle = findViewById(R.id.audiotexttitle);
        UploadTitle = findViewById(R.id.UploadTitle);

        ///AS WE START WE WILL GET PREVIOUS DATA AND ADD IT TO AUDIO NAME , IMAGE AND FILE

        Intent i = getIntent();

        if(i.getStringExtra("AudioList.audID") != null){
        //MEANS WE ARE GETTING DATA FROM AUDIO LIST PAGE WHEN USER CLICKS ON UPDATE
            Audio_ID = i.getStringExtra("AudioList.audID");
            Library_ID = i.getStringExtra("AudioList.LibID");
            input_audio_name.setText(i.getStringExtra("AudioList.audName"));

        }

        ///PROGRESS DIALOG INIT
        pd = new ProgressDialog(this);



        //SETTING ONCLICK LISTENER ------ ON IMAGE CLICK
        input_audio_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImagemethod();
            }
        });


        //SETTING ONCLICK LISTENER ------ ON AUDIOIMAGE CLICK TO TAKE AUDIO INPUT
        input_audio_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent audio = new Intent();
                audio.setType("audio/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    audio.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                choose_audio = true;
                choose_image = false;
                startActivityForResult(Intent.createChooser(audio,"Select Audio"),PICK_AUDIO);
            }
        });



        UploadTitle.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                ///GETTING PROGRESS DIALOG TO WORK
                pd.setMessage("Updating Audio...");
                pd.show();


                //OnCLICK SEND DATA TO AUDIO LIST PAGE
                String audio_name = input_audio_name.getText().toString();
                Uri imguri = ImageUri;
                Uri auduri = AudioUri;


                if(audio_name.equals("") || imguri == null || auduri == null ){
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Please Fill inputs",Toast.LENGTH_LONG).show();
                }
                else {
                    //HERE WE GET DATA TO BE UPDATED
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    String filePath_Id_image = storage_Path+"User_"+firebaseUser.getUid()+"/"+"Library_"+Library_ID+"/"+"Audio_"+
                            Audio_ID+"/"+Audio_ID+"_"+"Image";

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference storageReference1 = storageReference.child(filePath_Id_image);

                    storageReference1.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //GET URL FROM STORAGE
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            Uri downloadUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                Update_audio_details(audio_name, downloadUri, auduri, Audio_ID, Library_ID);
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
            }
        });

    }

    public void Update_audio_details(String Audio_name, Uri download_img_url, Uri audiouri,String Audio_ID, String Library_ID){

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String filePath_Id_audio = storage_Path+"User_"+firebaseUser.getUid()+"/"+"Library_"+Library_ID+"/"+"Audio_"+
                Audio_ID+"/"+Audio_ID+"_"+"Audio";

        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference1 = storageReference.child(filePath_Id_audio);

        storageReference1.putFile(audiouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                    hashMap.put("audio_image",download_img_url.toString());
                    hashMap.put("audio_file",download_audio_file_uri.toString());
                    hashMap.put("audio_ID",Audio_ID);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Libraries").child(Library_ID).child("Audio").child(Audio_ID);
                    databaseReference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Successfully Updated",Toast.LENGTH_SHORT).show();
                            finish();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && choose_image == true){

            //            ImageUri = data.getData(); ///Image is Picked

            pd.setMessage("Selecting Image...");
            pd.show();
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            //CONVERTING TO FILE Image and Audio
            File file_img = new File(result.getUri().getPath());
            Long fileSizeInBytes_img = file_img.length();

            float sizeKb = 1024.0f;
            float sizeMb = sizeKb * sizeKb;
            float sizeGb = sizeMb * sizeKb;

            DecimalFormat df = new DecimalFormat("0.00");

            if (fileSizeInBytes_img < sizeGb) {

                float size = Float.parseFloat(df.format(fileSizeInBytes_img / sizeMb));

                if (size >= 2) {
                    //INDICATES IMAGE IS GREATER THAN WHAT IS EXCEPTED
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Size is " + df.format
                            (fileSizeInBytes_img / sizeMb) + " MB"
                            + ",should be less than 2 MB", Toast.LENGTH_LONG).show();

                } else {

                    //IF IMAGE IS OKKK

                    ImageUri = result.getUri();
                    input_audio_image.setBorderWidth(10);
                    input_audio_image.setBorderColor(Color.YELLOW);//Change Border color and width
                    imgtexttitle.setText("Image Selected");
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),ImageUri);
                        input_audio_image.setImageBitmap(bitmap);
                        pd.dismiss();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
        if (requestCode == PICK_AUDIO && resultCode == RESULT_OK && choose_audio == true){
            pd.setMessage("Selecting Audio..");
            pd.show();
            ///Audio is Picked

            Cursor returnCursor = this.getContentResolver().query(data.getData(), null, null, null, null);
            assert returnCursor != null;
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            ((Cursor) returnCursor).moveToFirst();

            float sizeKb = 1024.0f;
            float sizeMb = sizeKb * sizeKb;
            float sizeGb = sizeMb * sizeKb;

            Long fileSizeInBytes_audio = returnCursor.getLong(sizeIndex);

            DecimalFormat df = new DecimalFormat("0.00");

            if (fileSizeInBytes_audio < sizeGb) {

                float size = Float.parseFloat(df.format(fileSizeInBytes_audio / sizeMb));

                if (size >= 5) {
                    //INDICATES AUDIO IS GREATER THAN WHAT IS EXCEPTED
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Size is "+df.format
                            (fileSizeInBytes_audio / sizeMb)+" MB"
                            +",should be less than 5 MB",Toast.LENGTH_LONG).show();

                } else {
                    //IF AUDIO IS OKK
                    AudioUri = data.getData();
                    input_audio_image2.setBorderWidth(10);
                    input_audio_image2.setBorderColor(Color.YELLOW);//Change Border color and width
                    audiotexttitle.setText("Audio Selected");
                    pd.dismiss();
                }
            }
        }


    }

    public  void CropImagemethod(){
        choose_image = true;
        choose_audio = false;
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
}