package com.example.teller;

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

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Custom_AudioInput_Activity extends AppCompatActivity {
    private EditText input_audio_name;
    private CircleImageView input_audio_image,input_audio_image2;
    private TextView imgtexttitle,audiotexttitle,UploadTitle;
    private final int PICK_AUDIO = 1;
    private boolean choose_audio = false;
    private boolean choose_image = false;
    Uri ImageUri;
    Uri AudioUri;
    ImageView BackToHome;
    public static final int KITKAT_VALUE = 1002;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_custom__audio_input_);

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
                audio.setAction(Intent.ACTION_OPEN_DOCUMENT);
                choose_audio = true;
                choose_image = false;
                startActivityForResult(Intent.createChooser(audio,"Select Audio"),PICK_AUDIO);


//                Intent audio = new Intent();
//
//                if (Build.VERSION.SDK_INT < 19) {
//                    audio = new Intent();
//                    audio.setAction(Intent.ACTION_GET_CONTENT);
//                    audio.setType("audio/*");
//                    startActivityForResult(audio, PICK_AUDIO);
//                } else {
//                    audio = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                    audio.addCategory(Intent.CATEGORY_OPENABLE);
//                    audio.setType("audio/*");
//                    startActivityForResult(audio, PICK_AUDIO);
//                }

            }
        });



        UploadTitle.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                //OnCLICK SEND DATA TO AUDIO LIST PAGE
                String audio_name = input_audio_name.getText().toString();
                Uri imguri = ImageUri;
                Uri auduri = AudioUri;


                if(audio_name.equals("") || imguri == null || auduri == null ){
                    Toast.makeText(getApplicationContext(),"Please Fill inputs",Toast.LENGTH_LONG).show();
                }
                else {

                    Intent i = new Intent(getApplicationContext(),AudioListPage.class);
                    i.putExtra("com.example.imusic.Custom_AudioInput_Activity.AudioName",audio_name);
                    i.putExtra("com.example.imusic.Custom_AudioInput_Activity.AudioImage",imguri.toString());
                    i.putExtra("com.example.imusic.Custom_AudioInput_Activity.AudioFile",auduri.toString());
                    startActivity(i);
                    finish();
                }
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && choose_image == true) {

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

            DecimalFormat df = null;
            df = new DecimalFormat("0.00");

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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageUri);
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

            DecimalFormat df = null;

            df = new DecimalFormat("0.00");

            if (fileSizeInBytes_audio < sizeGb) {

                float size = 0;
                size = Float.parseFloat(df.format(fileSizeInBytes_audio / sizeMb));

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