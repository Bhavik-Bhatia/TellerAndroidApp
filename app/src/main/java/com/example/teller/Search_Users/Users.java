package com.example.teller.Search_Users;

public class Users {
    String ID,fullname,bio,email,profile_img;

    public Users() {
    }

    public Users(String ID, String fullname, String bio, String email, String profile_img) {
        this.ID = ID;
        this.fullname = fullname;
        this.bio = bio;
        this.email = email;
        this.profile_img = profile_img;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}

