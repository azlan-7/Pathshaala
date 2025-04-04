package com.example.loginpage;

import android.os.Bundle;
import android.util.Log;
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

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.TextPart;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<MessageModel> messageList;

    private static final String MODEL_NAME = "gemini-2.0-flash"; // Use latest stable model
    private static final String API_KEY = "AIzaSyCb8zCUvH7AHKLeSDo6OLK5K0-cNbiLqQk";  // Replace with your actual API Key

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

        messageInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && chatRecyclerView.getAdapter() != null) {
                int itemCount = chatRecyclerView.getAdapter().getItemCount();
                if (itemCount > 0) {
                    chatRecyclerView.postDelayed(() ->
                            chatRecyclerView.smoothScrollToPosition(itemCount - 1), 200);
                }
            }
        });

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
            // Add user message to chat list
            messageList.add(new MessageModel(messageText, true)); // 'true' indicates user message
            messageList.add(new MessageModel("Typing...", false)); // Bot typing placeholder

            chatAdapter.notifyDataSetChanged();
            messageInput.setText("");

            // Call Gemini API with chat history
            generateText();
        }
    }

    private void generateText() {
        // Format chat history as a string
        StringBuilder chatHistory = new StringBuilder();
        for (MessageModel message : messageList) {
            if (message.isUser()) {
                chatHistory.append("User: ").append(message.getMessage()).append("\n");
            } else {
                if (!message.getMessage().equals("Typing...")) {  // Skip "Typing..." messages
                    chatHistory.append("Bot: ").append(message.getMessage()).append("\n");
                }
            }
        }

        // Initialize the GenerativeModel
        GenerativeModel model = new GenerativeModel(MODEL_NAME, API_KEY);

        // Call the generateContent method asynchronously
        model.generateContent(chatHistory.toString(), new Continuation<GenerateContentResponse>() {
            @Override
            public void resumeWith(Object result) {
                if (result instanceof GenerateContentResponse) {
                    Content content = ((GenerateContentResponse) result).getCandidates().get(0).getContent();
                    if (content != null) {
                        Log.d("GeminiAPI", "Generated Response: " + content.getParts().get(0).toString());

                        runOnUiThread(() -> {
                            // Remove "Typing..." message
                            if (!messageList.isEmpty()) {
                                messageList.remove(messageList.size() - 1);
                            }

                            // Add Gemini AI response
                            String botResponse = ((TextPart) content.getParts().get(0)).getText();
                            messageList.add(new MessageModel(botResponse, false)); // 'false' indicates bot message
                            chatAdapter.notifyDataSetChanged();
                            chatRecyclerView.smoothScrollToPosition(messageList.size() - 1);
                        });

                    }
                } else if (result instanceof Throwable) {
                    Log.e("GeminiAPI", "Error generating response", (Throwable) result);
                }
            }

            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE; // Required for Kotlin coroutines
            }
        });
    }
}