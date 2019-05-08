package com.example.dell.jsh;

import android.content.Context;

public class Posts {

    String uid , date , time , description , fullname,postimage , profileimage ;


    public Posts(){

    }
    public Posts(String uid, String date, String time, String description, String fullname, String postimage, String profileimage) {
        this.uid = uid;
        this.date = date;
        this.time = time;
        this.description = description;
        this.fullname = fullname;
        this.postimage = postimage;
        this.profileimage = profileimage;
    }

    public String getUid() {
        return uid;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPostimage() {
        return postimage;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public void setProfileimage( String profileimage) {
        this.profileimage = profileimage;

    }
}
