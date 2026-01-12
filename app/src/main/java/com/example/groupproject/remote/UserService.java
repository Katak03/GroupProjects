package com.example.groupproject.remote;

import com.example.groupproject.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import okhttp3.ResponseBody;
public interface UserService {
    @FormUrlEncoded
    @POST("api/login.php")
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

    @FormUrlEncoded
    @POST("api/get_user_profile.php")
    Call<ResponseBody> getUserProfile(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("api/add_exercise.php")
    Call<ResponseBody> addExercise(
            @Field("user_id") int userId,
            @Field("name") String name,
            @Field("category") String category,
            @Field("calories") int calories,
            @Field("details") String details
    );

    // 2. Deactivate Exercise
    @FormUrlEncoded
    @POST("api/update_exercise_status.php")
    Call<ResponseBody> updateExerciseStatus(
            @Field("exercise_id") int exerciseId,
            @Field("status") int status // 1 = Active, 0 = Inactive
    );

    @FormUrlEncoded
    @POST("api/register.php")
    Call<ResponseBody> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email,
            @Field("phone") String phone  // This matches $_POST['phone'] in the PHP
    );
}





