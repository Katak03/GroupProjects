package com.example.groupproject.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.groupproject.LoginActivity;
import com.google.gson.Gson;

public class SharedPrefManager {

    // 1. FIX: MATCH THE FILENAME WITH LOGIN ACTIVITY
    private static final String SHARED_PREF_NAME = "UserSession";

    private static final String KEY_USER_JSON = "user_object_json";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    /**
     * Store user data and set login status to TRUE
     */
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save User Object (for getting data later)
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER_JSON, userJson);

        // Save Login Flags (for auto-login check)
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt("user_id", user.getId()); // Save ID separately for easy access if needed

        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        // FIX: Check the boolean we actually save
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get the full User object
     */
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString(KEY_USER_JSON, null);

        if (userJson != null) {
            return new Gson().fromJson(userJson, User.class);
        }
        return null;
    }

    /**
     * Logout: Clear data and redirect
     */
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.commit(); // Force save immediately

        Intent intent = new Intent(mCtx, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mCtx.startActivity(intent);
    }
}