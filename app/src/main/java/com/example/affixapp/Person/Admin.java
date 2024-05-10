package com.example.affixapp.Person;

public class Admin extends Person{

    String UserID;

    public Admin(String username, String password, String email) {
        super(username, password, email);
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
