package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 1. Setup Window Insets (Edge to Edge display)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Display User Info
        TextView txtHello = findViewById(R.id.txtHello);
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        if (user != null) { // Safety check
            txtHello.setText("Hello " + user.getUsername() + " !");
        }

        // 3. EXERCISE BUTTON LOGIC (Must be inside onCreate!)
        Button btnExerciseList = findViewById(R.id.btnExerciseList);

        btnExerciseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to ExerciseListActivity
                Intent intent = new Intent(MainActivity.this, ExerciseListActivity.class);
                startActivity(intent);
            }
        });
        Button btnProfile = findViewById(R.id.btnViewProfile);
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }





    // Logout method (called via android:onClick in XML)
    public void doLogout(View view) {
        SharedPrefManager.getInstance(getApplicationContext()).logout();
        Toast.makeText(getApplicationContext(), "You have successfully logged out.", Toast.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }
}