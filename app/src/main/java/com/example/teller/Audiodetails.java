package com.example.teller;

public class Audiodetails {
    String audio_name;
    String audio_image;
    String audio_file;
    String audio_ID;

    public Audiodetails(String audio_name, String audio_image, String audio_file, String audio_ID) {
        this.audio_name = audio_name;
        this.audio_image = audio_image;
        this.audio_file = audio_file;
        this.audio_ID = audio_ID;
    }

    public String getAudio_ID() {
        return audio_ID;
    }

    public void setAudio_ID(String audio_ID) {
        this.audio_ID = audio_ID;
    }

    public Audiodetails() {
    }

    public String getAudio_name() {
        return audio_name;
    }

    public void setAudio_name(String audio_name) {
        this.audio_name = audio_name;
    }

    public String getAudio_image() {
        return audio_image;
    }

    public void setAudio_image(String audio_image) {
        this.audio_image = audio_image;
    }

    public String getAudio_file() {
        return audio_file;
    }

    public void setAudio_file(String audio_file) {
        this.audio_file = audio_file;
    }
}
