package com.example.affixapp.model;

import android.net.Uri;
import android.widget.ImageView;

import com.google.firebase.Timestamp;

public class ChatMessageModel {
    private String message, type;

    private String image;
    private String senderId;
    private Timestamp timestamp;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String type, String image, String senderId, Timestamp timestamp) {
        this.message = message;
        this.type = type;
        this.image = image;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}