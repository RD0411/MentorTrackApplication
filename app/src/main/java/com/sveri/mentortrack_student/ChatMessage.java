package com.sveri.mentortrack_student;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String sender, message;
    private Timestamp timestamp;

    public ChatMessage() {}  // Required for Firestore

    public ChatMessage(String sender, String message, Timestamp timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSender() { return sender; }
    public String getMessage() { return message; }
    public Timestamp getTimestamp() { return timestamp; }
}
