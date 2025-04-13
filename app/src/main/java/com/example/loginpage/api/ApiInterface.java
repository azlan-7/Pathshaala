package com.example.loginpage.api;

// ApiInterface.java
import com.example.loginpage.models.UserInfoItem;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface ApiInterface {
    @GET("getUserInfoByType")
    Call<List<UserInfoItem>> getUserWiseInfoByType(@Query("userid") int userid, @Query("typeid") int typeid);

    @GET("TeachersInfo/sp_UserWiseInfo")
    Call<List<UserInfoItem>> getAllUserInfo(@Query("userid") int userId); // ✅ new endpoint

    @GET("getUserInfo")
    Call<List<UserInfoItem>> getUserInfo(@Query("userid") int userId);

}
