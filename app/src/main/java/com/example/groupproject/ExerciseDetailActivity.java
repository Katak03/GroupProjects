package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciseDetailActivity extends AppCompatActivity {

    TextView tvName, tvCategory, tvType, tvDetails, tvCalories, tvExpPoints, tvStatus;
    int exerciseId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        // 1. Bind Views
        tvName = findViewById(R.id.tvExerciseName);
        tvCategory = findViewById(R.id.tvCategory);
        tvType = findViewById(R.id.tvType);
        tvDetails = findViewById(R.id.tvDetails);
        tvCalories = findViewById(R.id.tvCalories);
        tvExpPoints = findViewById(R.id.tvExpPoints);
        tvStatus = findViewById(R.id.tvIsActive);

        // 2. Receive Data
        Intent intent = getIntent();
        exerciseId = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("name");
        String details = intent.getStringExtra("details");
        int expPoints = intent.getIntExtra("exp_points", 0);
        int calories = intent.getIntExtra("calories", 0);
        String category = intent.getStringExtra("category");
        String type = intent.getStringExtra("type");
        int isActive = intent.getIntExtra("status", 1);

        // 3. Display Data
        if (name != null) tvName.setText(name);
        if (category != null) tvCategory.setText(category);
        if (type != null) tvType.setText(type);

        tvCalories.setText(calories + " cal");

        if (details != null && !details.isEmpty()) {
            tvDetails.setText(details);
        } else {
            tvDetails.setText("No details provided.");
        }

        tvExpPoints.setText(String.valueOf(expPoints));

        if (isActive == 1) {
            tvStatus.setText("Active");
        } else {
            tvStatus.setText("Inactive");
        }


        // 1. Add Button variable
        Button btnStartTimer;

        // Inside onCreate:
                btnStartTimer = findViewById(R.id.btnStartTimer);

        // 2. Button Click Listener
                btnStartTimer.setOnClickListener(v -> {
            Intent timerIntent = new Intent(ExerciseDetailActivity.this, ExerciseTimerActivity.class);
            // Pass the EXP points so the timer knows the reward rate
                    timerIntent.putExtra("exercise_id", exerciseId);
                    timerIntent.putExtra("exp_rate", expPoints);
            timerIntent.putExtra("exercise_name", name);
                    timerIntent.putExtra("calories_rate", calories);
            startActivity(timerIntent);
        });

    }
}