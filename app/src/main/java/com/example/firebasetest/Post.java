package com.example.firebasetest;

import java.util.Date;

public class Post extends PostId {
    private String image , user , caption, address, phone, website;
    private Date time;

    public String getImage() {
        return image;
    }

    public String getUser() {
        return user;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public String getCaption() {
        return caption;
    }

    public Date getTime() {
        return time;
    }
}
