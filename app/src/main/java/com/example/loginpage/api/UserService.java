package com.example.loginpage.api;

import okhttp3.Credentials;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserService {
    @POST("InsertUserDetails")
    Call<ResponseBody> insertUserDetails(
            @Header("Authorization") String authHeader,
            @Body UserRequest userRequest
    );

    // Helper method to create Basic Authentication header
    static String getAuthHeader() {
        return Credentials.basic("PATHSHALLAUSER", "PrD$7k9wMz2");
    }
}
