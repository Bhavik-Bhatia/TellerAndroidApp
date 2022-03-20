package com.example.teller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.RelativeDateTimeFormatter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioPlayPageActivity extends AppCompatActivity {
    TextView Audiogetname;
    ImageView BackToAudioList,forward,backward,AudioPlayimage;
    Button play_button,pause_button;
    SeekBar seekBar;
    TextView positiontime,totaltime;

    MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_audio_play_page);


        //GETTING AUDIO NAME, AUDIO ID , AUDIO FILE , AUDIO IMAGE ON THIS PAGE......
        Audiogetname = findViewById(R.id.Audiogetname);

        Intent i = getIntent();
        String AudioImage= i.getStringExtra("AudioList.audImage");
        String AudioName= i.getStringExtra("AudioList.audName");


        //SETTING THE AUDIO NAME
        Audiogetname.setText(AudioName);

        //SETTING THE AUDIO IMAGE
        AudioPlayimage = findViewById(R.id.AudioPlayimage);
        Picasso.get().load(AudioImage).into(AudioPlayimage);


        //BACK BUTTON PRESSED

        BackToAudioList = findViewById(R.id.BackToAudioList);
        BackToAudioList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mediaPlayer.reset();

                }

            }
        });

        //INIT
        play_button = findViewById(R.id.play_button);
        pause_button = findViewById(R.id.pause_button);
        seekBar = findViewById(R.id.seekBar);
        positiontime = findViewById(R.id.starttime);
        totaltime = findViewById(R.id.totaltime);

        forward = findViewById(R.id.forward);
        backward = findViewById(R.id.backward);

        try {
            runMedia();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runMedia() throws IOException {

        Intent i = getIntent();
        String AudioID= i.getStringExtra("AudioList.audID");
        String AudioImage= i.getStringExtra("AudioList.audImage");
        String AudioFile= i.getStringExtra("AudioList.audFile");

        Uri audio_uri = Uri.parse(AudioFile);

        mediaPlayer = MediaPlayer.create(this,audio_uri);
        runnable = new Runnable() {
            @Override
            public void run() {
                //SEEKBAR SETTING POSITION
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                //Handler post delay for 0.5sec
                handler.postDelayed(runnable,500);
            }
        };
        int duration = mediaPlayer.getDuration();

        //HERE WE GET DURATION OF AUDIO FILE WITH RIGHT FORMAT
        String sDuration = convertFormat(duration);

        //WE SET THE DURATION OF AUDIO FILE TO
        totaltime.setText(sDuration);

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //AUDIO IS PAUSED YOU CLICK ON PLAY(WHICH HIDES) AND PAUSE BUTTON IS NOW VISIBLE
                play_button.setVisibility(View.GONE);
                pause_button.setVisibility(View.VISIBLE);

                //Start Playling Media
                mediaPlayer.start();
                //Set Max to Seekbar
                seekBar.setMax(mediaPlayer.getDuration());
                //Handler thing
                handler.postDelayed(runnable,0);


            }
        });

        pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AUDIO IS PLAYING YOU CLICK ON PAUSE(WHICH HIDES) AND PLAY BUTTON IS NOW VISIBLE

                pause_button.setVisibility(View.GONE);
                play_button.setVisibility(View.VISIBLE);

                //Stop Media
                mediaPlayer.pause();
                //Stop handler
                handler.removeCallbacks(runnable);
            }
        });

        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int maxduration = duration-10000;
                if(mediaPlayer.isPlaying() && currentPosition < maxduration){
                    //FAST FORWARDING 10 Seconds

                    currentPosition = currentPosition + 10000;

                    //SETTING TEXT AUDIO POSItION TEXTVIEW
                    positiontime.setText(convertFormat(currentPosition));

                    //Updating Audio to that position
                    mediaPlayer.seekTo(currentPosition);

                }
            }
        });

        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();

                if(mediaPlayer.isPlaying() && currentPosition > 10000){
                    //FAST FORWARDING 10 Seconds
                    currentPosition = currentPosition - 10000;

                    //SETTING TEXT AUDIO POSItION TEXTVIEW
                    positiontime.setText(convertFormat(currentPosition));
                    //Updating Audio to that position
                    mediaPlayer.seekTo(currentPosition);

                }

            }
        });

        //SEEKBAR CUSTOMIZATION FOR AUDIO
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //IF USER TRACKS SEEKBAR
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }

                //UPDATING POSITION TEXT VIEW DEPENDING UPON TRACKED SEEKBAR
                positiontime.setText(convertFormat(mediaPlayer.getCurrentPosition()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Hiding pause button and showing play button
                play_button.setVisibility(View.VISIBLE);
                pause_button.setVisibility(View.GONE);
                mediaPlayer.seekTo(0);

            }
        });

    }

    @Override
    public void onBackPressed() {
        // make sure you have this outcommented
        // super.onBackPressed();
            finish();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            mediaPlayer.reset();
        }

    }


    @SuppressLint("DefaultLocale")
    public String convertFormat(int duration){
        //CHANGING THE FORMAT OF DURATION OF AUDIO FILE TO MIN:SEC AND RETURNING IT
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}