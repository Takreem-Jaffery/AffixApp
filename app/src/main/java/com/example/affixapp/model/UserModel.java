package com.example.affixapp.model;

import com.example.affixapp.Person.Person;
import com.google.firebase.Timestamp;

public class UserModel extends Person {
    private String email;
    private String username;
    private String password;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private int age;
    private String zodiacSign;
    private String gender;
    private String hobby;
    private String profession;
    private String fears;

    public UserModel() {
    }

    public UserModel(String email, String username, Timestamp createdTimestamp,String userId,String password) {
        this.email = email;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.password = password;
        this.age=0;
        this.zodiacSign=null;
        this.gender=null;
        this.hobby=null;
        this.profession=null;
        this.fears=null;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
    public void setTimestamp(Object timestamp) {
        if (timestamp instanceof Timestamp) {
            this.createdTimestamp = (Timestamp) timestamp;
        }
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String phone) {
        this.password = phone;
    }
    public String getZodiacSign() {
        return zodiacSign;
    }

    public void setZodiacSign(String zodiacSign) {
        this.zodiacSign = zodiacSign;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getFears() {
        return fears;
    }

    public void setFears(String fears) {
        this.fears = fears;
    }
}