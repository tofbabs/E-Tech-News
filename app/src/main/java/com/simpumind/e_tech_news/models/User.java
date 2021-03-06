package com.simpumind.e_tech_news.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by simpumind on 3/27/17.
 */

public class User {

    public String username;
    public String email;
    public String msisdn;
    public String physical_address;
    public String password;
    public String profileImage;
    public String signinType;


    public User(){

    }

    public User(String username, String email, String msisdn, String physical_address, String password, String userProfile, String signinType) {
        this.username = username;
        this.email = email;
        this.msisdn = msisdn;
        this.physical_address = physical_address;
        this.password = password;
        this.profileImage = userProfile;
        this.signinType = signinType;
    }

    public String getUserProfile() {
        return profileImage;
    }

    public void setUserProfile(String userProfile) {
        this.profileImage = userProfile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPhysical_address() {
        return physical_address;
    }

    public void setPhysical_address(String physical_address) {
        this.physical_address = physical_address;
    }

    public String getSigninType() {
        return signinType;
    }

    public void setSigninType(String signinType) {
        this.signinType = signinType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("msisdn", msisdn);
        result.put("email", email);
        result.put("physical_address", physical_address);
        result.put("password", password);
        result.put("profileImage", profileImage);
        result.put("signinType", signinType);
        return  result;
    }
}
