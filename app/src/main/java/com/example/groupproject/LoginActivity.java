package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.UserService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnLogin;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Check if user is already logged in
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            User user = SharedPrefManager.getInstance(this).getUser();
            navigateToHome(user.getRole());
            return;
        }

        // 2. Initialize Views
        edtUsername = findViewById(R.id.edtUsername); // Make sure ID matches XML
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        userService = ApiUtils.getUserService();

        // 3. Register Button Logic
        TextView tvRegister = findViewById(R.id.textViewRegister); // Make sure ID matches XML
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            });
        }

        // 4. Login Button Logic
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                doLogin(username, password);
            } else {
                Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void doLogin(String username, String password) {
        // This matches the 'Call<ResponseBody>' in UserService now
        Call<ResponseBody> call = userService.login(username, password);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // 1. Get the raw JSON from server
                        String jsonString = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonString);

                        // 2. Check the "success" flag from your PHP
                        if (jsonObject.getBoolean("success")) {

                            // 3. Get the 'user' object inside the response
                            JSONObject userJson = jsonObject.getJSONObject("user");

                            // 4. Create User object
                            User user = new User();
                            user.setId(userJson.getInt("id"));
                            user.setUsername(userJson.getString("username"));
                            user.setEmail(userJson.getString("email"));

                            // Get role (default to "user" if missing)
                            String role = userJson.optString("role", "user");
                            user.setRole(role);

                            // 5. Save to Shared Preferences
                            SharedPrefManager.getInstance(LoginActivity.this).userLogin(user);

                            // 6. Redirect
                            navigateToHome(role);

                        } else {
                            // Show the error message from PHP (e.g. "Invalid password")
                            String message = jsonObject.getString("message");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error parsing login data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome(String role) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        // Prevent going back to login screen
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}