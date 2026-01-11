package com.example.groupproject.remote;

import com.example.groupproject.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import okhttp3.ResponseBody;
public interface UserService {
    @FormUrlEncoded
    @POST("api/users/login")
    Call<User> login(@Field("username") String username, @Field("password") String
            password);


        @FormUrlEncoded
        @POST("api/add_completion.php")
        Call<ResponseBody> addCompletion(   // <--- Change to ResponseBody
                                            @Field("user_id") int userId,
                                            @Field("exercise_id") int exerciseId,
                                            @Field("duration_mins") int duration,
                                            @Field("calories_burned") int calories,
                                            @Field("exp_earned") int exp
        );

        @FormUrlEncoded
        @POST("api/update_points.php")
        Call<ResponseBody> updatePoints(    // <--- Change to ResponseBody
                                            @Field("user_id") int userId,
                                            @Field("points") int points
        );
    }




