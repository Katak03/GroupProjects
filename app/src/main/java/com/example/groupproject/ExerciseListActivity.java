package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.adapter.ExerciseAdapter;
import com.example.groupproject.model.Exercise;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.ExerciseService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseListActivity extends AppCompatActivity {

    private ExerciseService exerciseService;
    private RecyclerView rvExerciseList;
    private ExerciseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        // 1. Initialize API Service
        exerciseService = ApiUtils.getExerciseService();

        // 2. Bind RecyclerView
        rvExerciseList = findViewById(R.id.rvExerciseList);

        // 3. Set Layout Manager
        rvExerciseList.setLayoutManager(new LinearLayoutManager(this));
        rvExerciseList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // 4. IMPORTANT: Register the RecyclerView for the Context Menu (Long Click)
        registerForContextMenu(rvExerciseList);

        // 5. Fetch Data
        fetchExercises();
    }

    private void fetchExercises() {
        User user = SharedPrefManager.getInstance(this).getUser();
        String token = "";
        if (user != null && user.getToken() != null) {
            token = user.getToken();
        } else {
            Toast.makeText(this, "Please login first!", Toast.LENGTH_SHORT).show();
            return;
        }

        exerciseService.getAllExercises(token).enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Exercise> exercises = response.body();
                    adapter = new ExerciseAdapter(ExerciseListActivity.this, exercises);
                    rvExerciseList.setAdapter(adapter);
                } else {
                    Toast.makeText(ExerciseListActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Toast.makeText(ExerciseListActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    } // <--- END OF fetchExercises IS HERE. New methods start below.

    // -------------------------------------------------------------------
    // Context Menu Logic (Must be OUTSIDE fetchExercises)
    // -------------------------------------------------------------------

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Action");
        menu.add(0, 1, 0, "Details"); // groupId, itemId, order, title
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Ensure adapter is not null before checking selection
        if (adapter != null) {
            Exercise selectedExercise = adapter.getSelectedItem();
            if (selectedExercise != null && item.getTitle().equals("Details")) {
                doViewDetails(selectedExercise);
            }
        }
        return super.onContextItemSelected(item);
    }

    private void doViewDetails(Exercise exercise) {
        Intent intent = new Intent(this, ExerciseDetailActivity.class);


        intent.putExtra("id", exercise.getId());
        intent.putExtra("name", exercise.getExerciseName());
        intent.putExtra("category", exercise.getCategory());
        intent.putExtra("type", exercise.getExerciseType());
        intent.putExtra("calories", exercise.getCalories());
        intent.putExtra("details", exercise.getExerciseDetails());
        intent.putExtra("exp_points", exercise.getExpPoints());
        intent.putExtra("status", exercise.getIsActive());

        startActivity(intent);
    }
}