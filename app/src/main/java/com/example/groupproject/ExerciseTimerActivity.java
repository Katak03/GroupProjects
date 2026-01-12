package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
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
    private Button btnFinish;
    private TextView tvTitle, tvPreview;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_timer);

        userService = ApiUtils.getUserService();
        chronometer = findViewById(R.id.chronometer);
        btnFinish = findViewById(R.id.btnFinishWorkout);
        tvTitle = findViewById(R.id.tvTimerTitle);
        tvPreview = findViewById(R.id.tvPointsPreview);

        // Get Data from Intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("exercise_name");
        exerciseId = intent.getIntExtra("exercise_id", 0);
        expRate = intent.getIntExtra("exp_rate", 0);
        caloriesRate = intent.getIntExtra("calories_rate", 0);

        tvTitle.setText(name);
        tvPreview.setText("Earn " + expRate + " XP & " + caloriesRate + " cal per minute");

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        btnFinish.setOnClickListener(v -> {
            chronometer.stop();
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();

            // Calculate Minutes
            int minutes = (int) (elapsedMillis / 60000);

            // Ensure at least 1 minute is counted if workout is too short
            if (elapsedMillis > 1000 && minutes == 0) {
                minutes = 1;
            }

            // Calculate Total Calories and Points
            int totalPoints = minutes * expRate;
            int totalCalories = minutes * caloriesRate;

            if (minutes < 1) {
                Toast.makeText(this, "Workout too short!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();
                saveWorkoutHistory(minutes, totalCalories, totalPoints);
            }
        });
    }

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
                            int newLevel = obj.optInt("new_level", user.getGamification().getCurrentLevel());
                            int newExp = obj.optInt("current_exp", 0);
                            int newTotalExp = obj.optInt("total_exp", user.getGamification().getTotalExp());

                            updateGamification(user, newLevel, newExp, newTotalExp);

                            if (obj.optBoolean("leveled_up", false)) {
                                showLevelUpDialog(newLevel);
                            } else {
                                Toast.makeText(ExerciseTimerActivity.this, "Saved! Total XP: " + newTotalExp, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(ExerciseTimerActivity.this, "Error: " + obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ExerciseTimerActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
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

        // Save the updated user object to SharedPrefs
        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
    }

    private void showLevelUpDialog(int level) {
        new AlertDialog.Builder(this)
                .setTitle("🎉 LEVEL UP!")
                .setMessage("You reached Level " + level + "!")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
