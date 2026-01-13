package com.example.groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.adapter.UserAdapter;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;

    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Link the RecyclerView from XML
        recyclerView = findViewById(R.id.recyclerViewUsers);

        // IMPORTANT: You must set the LayoutManager, otherwise the list won't show
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUsers();

        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            logoutUser();
        });
    }

    private void loadUsers() {
        UserService service = ApiUtils.getUserService();
        Call<List<User>> call = service.getAllUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();

                    // Initialize the adapter with the list from the server
                    adapter = new UserAdapter(AdminDashboardActivity.this, users);

                    // Attach adapter to RecyclerView
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        // A. Clear shared preferences (session)
        SharedPrefManager.getInstance(this).logout();

        // B. Redirect back to Login Screen
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);

        // C. Add flags to prevent user from going back to dashboard on "Back" press
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}