package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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

public class ExerciseTimerActivity extends AppCompatActivity {

    private int exerciseId = 0;
    private int caloriesRate = 0;
    private int expRate = 0;

    private Chronometer chronometer;
    private Button btnFinish;
    private TextView tvTitle, tvPreview;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_timer);

        // 1. Initialize Service & Views
        userService = ApiUtils.getUserService();
        chronometer = findViewById(R.id.chronometer);
        btnFinish = findViewById(R.id.btnFinishWorkout);
        tvTitle = findViewById(R.id.tvTimerTitle);
        tvPreview = findViewById(R.id.tvPointsPreview);

        // 2. Get Data from previous screen
        Intent intent = getIntent();
        String name = intent.getStringExtra("exercise_name");
        exerciseId = intent.getIntExtra("exercise_id", 0);
        expRate = intent.getIntExtra("exp_rate", 0);
        caloriesRate = intent.getIntExtra("calories_rate", 0);

        tvTitle.setText(name);
        tvPreview.setText("Earn " + expRate + " XP & " + caloriesRate + " cal per minute");

        // 3. Start Timer
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        // 4. Handle Finish
        btnFinish.setOnClickListener(v -> {
            chronometer.stop();
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

            // Calculate Minutes (Test Mode: >5 seconds = 1 minute)
            int minutes;
            if (elapsedMillis > 5000 && elapsedMillis < 60000) {
                minutes = 1;
            } else {
                minutes = (int) (elapsedMillis / 60000);
            }

            if (minutes < 1) {
                Toast.makeText(this, "Workout too short! Run for 5+ seconds.", Toast.LENGTH_SHORT).show();
                // Resume timer logic if needed, or just let them press finish again
            } else {
                int totalPoints = minutes * expRate;
                int totalCalories = minutes * caloriesRate;

                Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();

                // STEP 1: Save History first
                saveWorkoutHistory(minutes, totalCalories, totalPoints);
            }
        });
    }

    // --- STEP 1: Save to 'completion' table ---
    private void saveWorkoutHistory(int minutes, int calories, int points) {
        User user = SharedPrefManager.getInstance(this).getUser();

        if(exerciseId == 0) {
            Toast.makeText(this, "Error: Exercise ID missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Note: Make sure addCompletion returns Call<ResponseBody> in UserService just in case
        Call<ResponseBody> call = userService.addCompletion(
                user.getId(),
                exerciseId,
                minutes,
                calories,
                points
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // History saved! Now STEP 2: Update Points & Level
                    updateUserPoints(points);
                } else {
                    Toast.makeText(ExerciseTimerActivity.this, "Failed to save history: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ExerciseTimerActivity.this, "Network Error (History)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- STEP 2: Update Points & Check Level ---
    private void updateUserPoints(int pointsToAdd) {
        User user = SharedPrefManager.getInstance(this).getUser();

        // Ensure UserService.updatePoints returns Call<ResponseBody>
        Call<ResponseBody> call = userService.updatePoints(user.getId(), pointsToAdd);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // Parse the JSON from update_points.php
                        String jsonString = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonString);

                        boolean success = jsonObject.getBoolean("success");

                        if (success) {
                            boolean leveledUp = jsonObject.optBoolean("leveled_up", false);
                            int newLevel = jsonObject.optInt("new_level", 1);

                            if (leveledUp) {
                                showLevelUpDialog(newLevel);
                            } else {
                                Toast.makeText(ExerciseTimerActivity.this, "Saved! +"+pointsToAdd+" XP", Toast.LENGTH_SHORT).show();
                                finish(); // Close screen
                            }
                        } else {
                            Toast.makeText(ExerciseTimerActivity.this, "Server Error: " + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ExerciseTimerActivity.this, "Network Error (Points)", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // --- HELPER: Show Level Up Dialog ---
    private void showLevelUpDialog(int level) {
        new AlertDialog.Builder(this)
                .setTitle("🎉 LEVEL UP!")
                .setMessage("Congratulations! You have reached Level " + level + "!")
                .setPositiveButton("AWESOME", (dialog, which) -> {
                    finish(); // Close activity when they click OK
                })
                .setCancelable(false) // User must click OK
                .show();
    }
}