package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.UserService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseDetailActivity extends AppCompatActivity {

    // 1. Declare UI Components and Service
    TextView tvName, tvCategory, tvType, tvDetails, tvCalories, tvExpPoints, tvStatus;
    Button btnDeactivate, btnStartTimer;
    UserService userService;
    int exerciseId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        // 2. Initialize Service
        userService = ApiUtils.getUserService();

        // 3. Bind Views
        tvName = findViewById(R.id.tvExerciseName);
        tvCategory = findViewById(R.id.tvCategory);
        tvType = findViewById(R.id.tvType);
        tvDetails = findViewById(R.id.tvDetails);
        tvCalories = findViewById(R.id.tvCalories);
        tvExpPoints = findViewById(R.id.tvExpPoints);
        tvStatus = findViewById(R.id.tvIsActive);
        btnDeactivate = findViewById(R.id.btnDeactivate);
        btnStartTimer = findViewById(R.id.btnStartTimer);

        // 4. Receive Data from Intent
        Intent intent = getIntent();
        exerciseId = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("name");
        String details = intent.getStringExtra("details");
        int expPoints = intent.getIntExtra("exp_points", 0);
        int calories = intent.getIntExtra("calories", 0);
        String category = intent.getStringExtra("category");
        String type = intent.getStringExtra("type"); // This holds "DEFAULT" or "CUSTOM"
        int isActive = intent.getIntExtra("status", 1);

        // 5. Display Data
        if (name != null) tvName.setText(name);
        if (category != null) tvCategory.setText(category);
        if (type != null) tvType.setText(type);
        tvCalories.setText(calories + " cal");
        tvExpPoints.setText(String.valueOf(expPoints));
        tvDetails.setText((details != null && !details.isEmpty()) ? details : "No details provided.");

        if (isActive == 1) {
            tvStatus.setText("Active");
        } else {
            tvStatus.setText("Inactive");
        }

        // 6. Logic: Only show Deactivate button if Active AND NOT a default exercise
        // We check if type is NOT null and NOT "DEFAULT"
        if (isActive == 1 && !"DEFAULT".equalsIgnoreCase(type)) {
            btnDeactivate.setVisibility(View.VISIBLE);
        } else {
            btnDeactivate.setVisibility(View.GONE);
        }

        // 7. Timer Button Listener
        btnStartTimer.setOnClickListener(v -> {
            Intent timerIntent = new Intent(ExerciseDetailActivity.this, ExerciseTimerActivity.class);
            timerIntent.putExtra("exercise_id", exerciseId);
            timerIntent.putExtra("exp_rate", expPoints);
            timerIntent.putExtra("exercise_name", name);
            timerIntent.putExtra("calories_rate", calories);
            startActivity(timerIntent);
        });

        // 8. Deactivate Button Listener
        btnDeactivate.setOnClickListener(v -> {
            toggleExerciseStatus(exerciseId, 0); // 0 = Inactive
        });
    }

    private void toggleExerciseStatus(int id, int newStatus) {
        Call<ResponseBody> call = userService.updateExerciseStatus(id, newStatus);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ExerciseDetailActivity.this, "Exercise Deactivated", Toast.LENGTH_SHORT).show();

                    // Update UI immediately
                    tvStatus.setText("Inactive");
                    btnDeactivate.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ExerciseDetailActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(ExerciseDetailActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}