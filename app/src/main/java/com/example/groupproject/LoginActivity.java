package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.groupproject.model.ErrorResponse;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.UserService;
import com.google.gson.Gson;

import java.io.IOException;

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

        // if the user is already logged in we will directly start
        // the main activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();// stop this LoginActivity
            startActivity(new Intent(this, MainActivity.class));
            return;
        }


        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (validateLogin(username, password)) {
                    doLogin(username, password);
                }
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    private void doLogin(String username, String password) {

        // IMPORTANT: use generics so response.body() is a User
        Call<User> call = userService.login(username, password);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {

                    User user = response.body();

                    // Guard: server might return empty body (causes JSON parse errors)
                    if (user == null) {
                        displayToast("Login failed: empty response from server");
                        return;
                    }

                    if (user.getToken() != null && !user.getToken().isEmpty()) {

                        displayToast("Login successful");

                        // ✅ Store in SharedPreferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        // ✅ Go to MainActivity
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    } else {
                        displayToast("Login failed: token not received");
                    }

                } else if (response.errorBody() != null) {

                    // Parse error response body (JSON)
                    try {
                        String errorResp = response.errorBody().string();
                        ErrorResponse e = new Gson().fromJson(errorResp, ErrorResponse.class);

                        if (e != null && e.getError() != null) {
                            displayToast(e.getError().getMessage());
                        } else {
                            displayToast("Login failed (HTTP " + response.code() + ")");
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        displayToast("Login failed: error reading server response");
                    }
                } else {
                    displayToast("Login failed (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                displayToast("Error connecting to server.");
                displayToast(t.getMessage());
            }
        });
    }

    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}