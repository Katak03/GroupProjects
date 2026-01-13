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
import com.example.groupproject.model.Gamification;
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
    private Button btnFinish, btnPause;
    private TextView tvTitle, tvPreview;
    private UserService userService;

    // Timer Variables
    private boolean isTimerRunning;
    private long pauseOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_timer);

        userService = ApiUtils.getUserService();
        chronometer = findViewById(R.id.chronometer);
        btnFinish = findViewById(R.id.btnFinishWorkout);
        btnPause = findViewById(R.id.btnPauseResume); // New Button
        tvTitle = findViewById(R.id.tvTimerTitle);
        tvPreview = findViewById(R.id.tvPointsPreview);

        // Get Data
        Intent intent = getIntent();
        String name = intent.getStringExtra("exercise_name");
        exerciseId = intent.getIntExtra("exercise_id", 0);
        expRate = intent.getIntExtra("exp_rate", 0);
        caloriesRate = intent.getIntExtra("calories_rate", 0);

        tvTitle.setText(name);
        tvPreview.setText("Earn " + expRate + " XP & " + caloriesRate + " cal per minute");

        // Start Timer automatically
        startTimer();

        // Pause/Resume Button Logic
        btnPause.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                resumeTimer();
            }
        });

        // Finish Button Logic
        btnFinish.setOnClickListener(v -> {
            // Stop timer first
            if (isTimerRunning) {
                chronometer.stop();
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                isTimerRunning = false;
            }

            // Calculate actual time based on the offset
            long elapsedMillis = pauseOffset;
            int minutes = (int) (elapsedMillis / 60000);

            // Safety check: Give 1 min credit if it was close (optional)
            if (elapsedMillis > 30000 && minutes == 0) {
                minutes = 1;
            }

            int totalPoints = minutes * expRate;
            int totalCalories = minutes * caloriesRate;

            if (minutes < 1) {
                Toast.makeText(this, "Workout too short (< 1 min)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();
                saveWorkoutHistory(minutes, totalCalories, totalPoints);
            }
        });
    }

    // --- Helper Methods for Timer ---

    private void startTimer() {
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
        isTimerRunning = true;
        btnPause.setText("Pause");
    }

    private void pauseTimer() {
        chronometer.stop();
        pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
        isTimerRunning = false;
        btnPause.setText("Resume");
    }

    private void resumeTimer() {
        chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        chronometer.start();
        isTimerRunning = true;
        btnPause.setText("Pause");
    }

    // --- Network Logic ---

    private void saveWorkoutHistory(int minutes, int calories, int points) {
        User user = SharedPrefManager.getInstance(this).getUser();

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
                    try {
                        String jsonString = response.body().string();
                        JSONObject obj = new JSONObject(jsonString);

                        if (obj.getBoolean("success")) {
                            // --- SUCCESS LOGIC ---

                            // Handle potential null Gamification object safely
                            int currentLevel = (user.getGamification() != null) ? user.getGamification().getCurrentLevel() : 1;
                            int totalExp = (user.getGamification() != null) ? user.getGamification().getTotalExp() : 0;

                            int newLevel = obj.optInt("new_level", currentLevel);
                            int newExp = obj.optInt("current_exp", 0);
                            int newTotalExp = obj.optInt("total_exp", totalExp);

                            updateGamification(user, newLevel, newExp, newTotalExp);

                            if (obj.optBoolean("leveled_up", false)) {
                                showLevelUpDialog(newLevel);
                                // Note: finish() is called inside the Dialog's "OK" button
                            } else {
                                Toast.makeText(ExerciseTimerActivity.this, "Great Job! + " + points + " XP", Toast.LENGTH_SHORT).show();
                                finish(); // <--- GO BACK
                            }
                        } else {
                            // --- SERVER LOGIC ERROR (e.g., Database failed) ---
                            Toast.makeText(ExerciseTimerActivity.this, "Saved locally (Server Error: " + obj.getString("message") + ")", Toast.LENGTH_LONG).show();
                            finish(); // <--- ADD THIS: Force go back even if server complains
                        }
                    } catch (Exception e) {
                        // --- PARSING ERROR ---
                        e.printStackTrace();
                        Toast.makeText(ExerciseTimerActivity.this, "Error processing data, but workout done.", Toast.LENGTH_SHORT).show();
                        finish(); // <--- ADD THIS: Force go back on crash/error
                    }
                } else {
                    // --- HTTP ERROR (e.g., 404, 500) ---
                    Toast.makeText(ExerciseTimerActivity.this, "Server error code: " + response.code(), Toast.LENGTH_SHORT).show();
                    finish(); // <--- ADD THIS
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // --- NETWORK ERROR (No internet) ---
                Toast.makeText(ExerciseTimerActivity.this, "Network failed. Workout not saved online.", Toast.LENGTH_SHORT).show();
                finish(); // <--- ADD THIS: Allow user to leave even if offline
            }
        });
    }
    private void updateGamification(User user, int newLevel, int newExp, int newTotalExp) {
        if (user.getGamification() == null) {
            user.setGamification(new Gamification());
            user.getGamification().setId(user.getId());
        }
        user.getGamification().setCurrentLevel(newLevel);
        user.getGamification().setCurrentExp(newExp);
        user.getGamification().setTotalExp(newTotalExp);
        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
    }

    private void showLevelUpDialog(int level) {
        new AlertDialog.Builder(this)
                .setTitle("🎉 LEVEL UP!")
                .setMessage("You reached Level " + level + "!")
                .setPositiveButton("OK", (dialog, which) -> {
                    // When they click OK on the level up dialog, go back
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    // Safety: If user presses back button while timer is running
    @Override
    public void onBackPressed() {
        if (isTimerRunning) {
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Workout?")
                    .setMessage("Timer is running. Are you sure you want to stop?")
                    .setPositiveButton("Yes", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("No", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}