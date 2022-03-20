package com.example.teller.UserPosts;

public class ModalUserPosts {
    String LIB_ID;
    String Library_name;
    String UID;


    public ModalUserPosts() {
    }

    public ModalUserPosts(String LIB_ID, String library_name, String UID) {
        this.LIB_ID = LIB_ID;
        Library_name = library_name;
        this.UID = UID;
    }

    public String getLIB_ID() {
        return LIB_ID;
    }

    public void setLIB_ID(String LIB_ID) {
        this.LIB_ID = LIB_ID;
    }

    public String getLibrary_name() {
        return Library_name;
    }

    public void setLibrary_name(String library_name) {
        Library_name = library_name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
