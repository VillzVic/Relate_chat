package com.example.appzonepc2.relate.model;

/**
 * Created by appzonepc2 on 23/02/2018.
 */


public class userListDetails {
    private String user_name;
    private String user_status;
    private String user_image;

    public userListDetails() {
    }

    public userListDetails(String user_name, String user_status, String user_image) {
        this.user_name = user_name;
        this.user_status = user_status;
        this.user_image = user_image;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUsername(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUserStatus(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUserImage(String user_image) {
        this.user_image = user_image;
    }
}
