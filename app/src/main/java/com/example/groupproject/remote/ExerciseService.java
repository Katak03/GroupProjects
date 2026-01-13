package com.example.groupproject.remote;

import com.example.groupproject.model.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExerciseService {

    /**
     * URL: http://YOUR_DOMAIN/api/exercise
     * Method: GET
     * Action: Retrieves a list of all exercises.
     */
    @GET("api/exercise.php")
    Call<List<Exercise>> getAllExercises(@Header("api-key") String apiKey, @Query("user_id") int userId);

    @GET("api/exercise/{id}")
    Call<Exercise> getExercise(@Header("api-key") String apiKey, @Path("id") int id);
    // If you need to search (Optional/Extra)
    // @GET("api/exercise/search/{name}")
    // Call<List<Exercise>> searchExercises(@Header("api-key") String apiKey, @Path("name") String name);
}