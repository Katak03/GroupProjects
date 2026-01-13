package com.example.groupproject.remote;

public class ApiUtils {
    // REST API server URL
    public static final String BASE_URL =
            "https://cuter-meghan-inutilely.ngrok-free.dev/prestige/";

    // return UserService instance
    // return UserService instance
    public static UserService getUserService() {
        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }

    public static ExerciseService getExerciseService() {
        return RetrofitClient.getClient(BASE_URL).create(ExerciseService.class);
    }

}
