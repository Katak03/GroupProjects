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

    TextView tvUser, tvEmail, tvPhone, tvLevel, tvExp, tvTotal,tvCalories;
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
        tvExp = findViewById(R.id.tvCurrentExp);
        tvTotal = findViewById(R.id.tvTotalExp);
        tvCalories = findViewById(R.id.tvTotalCalories);
        Button btnBack = findViewById(R.id.btnBack);

        userService = ApiUtils.getUserService();
        User user = SharedPrefManager.getInstance(this).getUser();

        // 2. Fetch Data from Server
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
                            // 3. Set User Info
                            tvUser.setText(obj.getString("username"));
                            tvEmail.setText(obj.getString("email"));
                            tvPhone.setText(obj.getString("phone")); // Will show "N/A" if null

                            // 4. Set Gamification Stats
                            tvLevel.setText(String.valueOf(obj.getInt("level")));
                            tvTotal.setText(String.valueOf(obj.getInt("total_exp")));
                            tvExp.setText("Current Progress: " + obj.getInt("current_exp") + " XP");
                            int cals = obj.optInt("calories", 0);
                            tvCalories.setText("Total Burned: " + cals + " kcal");
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