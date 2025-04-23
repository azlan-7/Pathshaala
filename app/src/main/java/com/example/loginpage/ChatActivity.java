package com.example.loginpage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

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
    private ImageView botImage;
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
        botImage = findViewById(R.id.botImage);
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, this);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        if (!messageList.isEmpty()) {
            botImage.setVisibility(View.GONE); // Hide the bot image
        } else {
            botImage.setVisibility(View.VISIBLE); // Show if empty
        }


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
            generateText(messageText);

        }
    }

    private void generateText(String latestUserMessage) {
        Log.d("GeminiPrompt", "Prompt to Gemini: " + latestUserMessage);

        GenerativeModel model = new GenerativeModel(MODEL_NAME, API_KEY);

        // Timeout handler setup
        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        Runnable timeoutRunnable = () -> {
            if (messageList.get(messageList.size() - 1).getMessage().equals("Typing...")) {
                messageList.remove(messageList.size() - 1);
                messageList.add(new MessageModel("Still thinking... Please try rephrasing your question if there's no response.", false));
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.smoothScrollToPosition(messageList.size() - 1);
            }
        };

        // Post timeout task (20 seconds)
        timeoutHandler.postDelayed(timeoutRunnable, 20000); // 20,000 milliseconds

        model.generateContent(latestUserMessage, new Continuation<GenerateContentResponse>() {
            @Override
            public void resumeWith(Object result) {
                // Cancel timeout if response received in time
                timeoutHandler.removeCallbacks(timeoutRunnable);

                if (result instanceof GenerateContentResponse) {
                    Content content = ((GenerateContentResponse) result).getCandidates().get(0).getContent();

                    if (content != null && !content.getParts().isEmpty()) {
                        String botResponse = ((TextPart) content.getParts().get(0)).getText();
                        Log.d("GeminiResponse", "Bot reply: " + botResponse);

                        runOnUiThread(() -> {
                            messageList.remove(messageList.size() - 1); // Remove "Typing..."
                            messageList.add(new MessageModel(botResponse, false)); // Add bot reply
                            chatAdapter.notifyDataSetChanged();
                            chatRecyclerView.smoothScrollToPosition(messageList.size() - 1);
                        });

                    } else {
                        Log.e("GeminiResponse", "Empty content or no parts returned!");
                    }

                } else if (result instanceof Throwable) {
                    Throwable error = (Throwable) result;
                    Log.e("GeminiError", "Error while generating response", error);

                    runOnUiThread(() -> {
                        messageList.remove(messageList.size() - 1);
                        messageList.add(new MessageModel("Hmm... Couldn't fetch a response this time. Try again in a moment!", false));
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.smoothScrollToPosition(messageList.size() - 1);
                    });
                }
            }

            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }
        });
    }


}
