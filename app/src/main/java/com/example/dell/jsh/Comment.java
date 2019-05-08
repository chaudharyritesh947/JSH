package com.example.dell.jsh;

public class Comment {


  public String  date , time ,userName , comment ,admin,timeAdmin;
    public Comment(){

    }

    public Comment(String date, String time, String userName, String comment, String admin,String timeAdmin) {
        this.date = date;
        this.time = time;
        this.userName = userName;
        this.comment = comment;
        this.admin = admin;
        this.timeAdmin=timeAdmin;
    }
    public String getTimeAdmin(){
      return timeAdmin;
  }
  public void setTimeAdmin(String timeAdmin){
      this.timeAdmin=timeAdmin;
  }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = "we are on it ";
    }



}
