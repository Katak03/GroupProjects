package com.example.groupproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.UserService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExerciseActivity extends AppCompatActivity {

    EditText etName, etCategory, etCalories, etDetails;
    Button btnSave;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        userService = ApiUtils.getUserService();
        etName = findViewById(R.id.etExName);
        etCategory = findViewById(R.id.etExCategory);
        etCalories = findViewById(R.id.etExCalories);
        etDetails = findViewById(R.id.etExDetails);
        btnSave = findViewById(R.id.btnSaveExercise);

        btnSave.setOnClickListener(v -> saveExercise());
    }

    private void saveExercise() {
        String name = etName.getText().toString().trim();
        String cat = etCategory.getText().toString().trim();
        String calStr = etCalories.getText().toString().trim();
        String detail = etDetails.getText().toString().trim();

        if (name.isEmpty() || calStr.isEmpty()) {
            Toast.makeText(this, "Name and Calories are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int calories = Integer.parseInt(calStr);
        User user = SharedPrefManager.getInstance(this).getUser();

        Call<ResponseBody> call = userService.addExercise(user.getId(), name, cat, calories, detail);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        // Get the actual string response from PHP
                        String res = response.body().string();
                        // PRINT THIS TO LOGCAT
                        android.util.Log.d("AddExercise", "Server Response: " + res);

                        // Check if the JSON says success
                        if (res.contains("true")) {
                            Toast.makeText(AddExerciseActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddExerciseActivity.this, "Server Error: " + res, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String errorBody = response.errorBody().string();
                        android.util.Log.e("AddExercise", "Error: " + errorBody);
                        Toast.makeText(AddExerciseActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AddExerciseActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}