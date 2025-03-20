package com.example.loginpage;

import okhttp3.*;
import java.io.IOException;

public class NextGWhatsAppService {
    private static final String API_URL = "https://waba.nexgplatforms.com/send-message";
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your actual API key

    public static void sendWhatsAppMessage(String phoneNumber, String userName) {
        OkHttpClient client = new OkHttpClient();

        String jsonPayload = "{"
                + "\"to\":\"" + phoneNumber + "\","
                + "\"type\":\"template\","
                + "\"template\":{"
                + "\"name\":\"greeting_message\","
                + "\"language\":{\"code\":\"en\"},"
                + "\"components\":[{"
                + "\"type\":\"body\","
                + "\"parameters\":["
                + "{\"type\":\"text\",\"text\":\"" + userName + "\"},"
                + "{\"type\":\"text\",\"text\":\"Pathshaala App\"}"
                + "]"
                + "}]"
                + "}"
                + "}";

        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Response: " + response.body().string());
            }
        });
    }
}
