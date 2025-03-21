package com.example.loginpage;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.loginpage.adapters.ChatAdapter;
import com.example.loginpage.models.MessageModel;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<MessageModel> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, this);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            if(messageText.equalsIgnoreCase("Hello")
                    || messageText.equalsIgnoreCase("Hi")
                    || messageText.equalsIgnoreCase("Hey")){
                messageList.add(new MessageModel(messageText, true)); // User message
                messageList.add(new MessageModel("Typing...", false)); // Bot typing placeholder

                chatAdapter.notifyDataSetChanged();
                messageInput.setText("");

                // Simulate bot response after delay
                chatRecyclerView.postDelayed(() -> {
                    messageList.remove(messageList.size() - 1); // Remove "Typing..."
                    messageList.add(new MessageModel("Hello, I am EasyBot. How can I help you?", false)); // Bot response
                    chatAdapter.notifyDataSetChanged();
                }, 2000);
            }
            else{
                messageList.add(new MessageModel(messageText, true)); // User message
                messageList.add(new MessageModel("Typing...", false)); // Bot typing placeholder

                chatAdapter.notifyDataSetChanged();
                messageInput.setText("");

                // Simulate bot response after delay
                chatRecyclerView.postDelayed(() -> {
                    messageList.remove(messageList.size() - 1); // Remove "Typing..."
                    messageList.add(new MessageModel("I am Easybot. You said: " + messageText +
                            ".\nI'm sorry, I don't have a response for that as of now.", false)); // Bot response
                    chatAdapter.notifyDataSetChanged();
                }, 2000);
            }

        }
    }
}