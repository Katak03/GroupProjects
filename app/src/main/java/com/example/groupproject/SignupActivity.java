package com.example.groupproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.UserService;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    EditText editUsername, editPwd, editPwd2, editEmail, editPnum;
    Button confirmSignupButton;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 1. Initialize Retrofit Service
        userService = ApiUtils.getUserService();

        // 2. Bind Views
        editUsername = findViewById(R.id.editUsernameSignup);
        editPwd = findViewById(R.id.editPwdSignup);
        editPwd2 = findViewById(R.id.editpwdSignup2);
        editEmail = findViewById(R.id.editEmail);
        editPnum = findViewById(R.id.editpnum);
        confirmSignupButton = findViewById(R.id.confrimSignupButton);

        // 3. Set Listener
        confirmSignupButton.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String pwd1 = editPwd.getText().toString().trim();
            String pwd2 = editPwd2.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String phone = editPnum.getText().toString().trim();

            if (validateInput(username, pwd1, pwd2, email, phone)) {
                performSignup(username, pwd1, email, phone);
            }
        });
    }

    private boolean validateInput(String u, String p1, String p2, String e, String ph) {
        if (u.isEmpty() || p1.isEmpty() || e.isEmpty() || ph.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!p1.equals(p2)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performSignup(String username, String password, String email, String phone) {
        Call<ResponseBody> call = userService.register(username, password, email, phone);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // We get a JSON string like {"success":true, "message":"..."}
                        String s = response.body().string();
                        JSONObject jsonObject = new JSONObject(s);
                        boolean success = jsonObject.getBoolean("success");
                        String message = jsonObject.getString("message");

                        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();

                        if (success) {
                            finish(); // Go back to Login screen
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SignupActivity.this, "JSON Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Network Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}