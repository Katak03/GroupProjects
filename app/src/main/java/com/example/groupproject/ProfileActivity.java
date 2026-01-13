package com.example.groupproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.UserService;
import org.json.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    // Added tvWorkouts and tvMinutes
    TextView tvUser, tvEmail, tvPhone, tvLevel, tvExp, tvTotal, tvCalories, tvWorkouts, tvMinutes;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Initialize Views
        tvUser = findViewById(R.id.tvProfileUsername);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvPhone = findViewById(R.id.tvProfilePhone);

        tvLevel = findViewById(R.id.tvCurrentLevel);
        tvTotal = findViewById(R.id.tvTotalExp);
        tvExp = findViewById(R.id.tvCurrentExp);
        tvCalories = findViewById(R.id.tvTotalCalories);

        // NEW VIEWS
        tvWorkouts = findViewById(R.id.tvTotalWorkouts);
        tvMinutes = findViewById(R.id.tvTotalMinutes);

        Button btnBack = findViewById(R.id.btnBack);

        userService = ApiUtils.getUserService();
        User user = SharedPrefManager.getInstance(this).getUser();

        loadUserProfile(user.getId());

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserProfile(int userId) {
        Call<ResponseBody> call = userService.getUserProfile(userId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String json = response.body().string();
                        JSONObject obj = new JSONObject(json);

                        if (obj.getBoolean("success")) {
                            // Basic Info
                            tvUser.setText(obj.getString("username"));
                            tvEmail.setText(obj.getString("email"));
                            tvPhone.setText(obj.getString("phone"));

                            // Stats
                            tvLevel.setText(String.valueOf(obj.getInt("level")));
                            tvTotal.setText(String.valueOf(obj.getInt("total_exp")));
                            tvExp.setText("Current Progress: " + obj.getInt("current_exp") + " XP");

                            // Calories
                            int cals = obj.optInt("calories", 0);
                            tvCalories.setText("Total Burned: " + cals + " kcal");

                            // --- NEW STATS ---
                            int workouts = obj.optInt("workouts", 0);
                            int minutes = obj.optInt("total_minutes", 0);

                            tvWorkouts.setText(String.valueOf(workouts));
                            tvMinutes.setText(String.valueOf(minutes));

                        } else {
                            Toast.makeText(ProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Error parsing profile", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}