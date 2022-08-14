package com.codefury.starthub;

public class Users {
    String username;
    String uid;
    String name;
    String device_token;
    String mobile;



    public Users(){}

    public Users(String username, String uid, String name) {
        this.username = username;
        this.uid = uid;
        this.name = name;
        this.mobile = mobile;
        this.device_token=device_token;
    }
    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
