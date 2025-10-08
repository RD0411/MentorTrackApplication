package com.sveri.mentortrack_student;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.*;
import java.util.*;

public class ChatActivity extends AppCompatActivity {

    private String studentEmail, mentorEmail;
    private FirebaseFirestore db;
    private RecyclerView chatRecyclerView;
    private TextInputEditText messageEditText;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private ListenerRegistration messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        db = FirebaseFirestore.getInstance();

        // Get student email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        studentEmail = prefs.getString("email", null);

        if (studentEmail == null) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get mentor email
        db.collection("students").document(studentEmail)
                .get()
                .addOnSuccessListener(doc -> {
                    mentorEmail = doc.getString("mentoremail");
                    if (mentorEmail != null) {
                        setupChat();
                    } else {
                        Toast.makeText(this, "Mentor not assigned", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupChat() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, studentEmail);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        String chatId = studentEmail + "_" + mentorEmail;
        CollectionReference messagesRef = db.collection("chats")
                .document(chatId)
                .collection("messages");

        messageListener = messagesRef
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("Chat", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null) {
                        messageList.clear();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            ChatMessage msg = doc.toObject(ChatMessage.class);
                            messageList.add(msg);
                        }
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("sender", studentEmail);
                msg.put("message", message);
                msg.put("timestamp", FieldValue.serverTimestamp());

                messagesRef.add(msg)
                        .addOnSuccessListener(docRef -> messageEditText.setText(""))
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageListener != null) {
            messageListener.remove();
        }
    }
}
