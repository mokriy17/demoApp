package com.example.demoapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthActivity extends AppCompatActivity {
    private EditText etEmail, etPass;
    private AppDatabase db;
    private SessionManager session;
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        etEmail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_password);
        db = AppDatabase.getInstance(this);
        session = new SessionManager(this);

        findViewById(R.id.btn_login).setOnClickListener(v -> login());
        TextView tvReg = findViewById(R.id.tv_go_reg);
        tvReg.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Некорректный Email");
            return;
        }
        if (pass.length() < 6) {
            etPass.setError("Минимум 6 символов");
            return;
        }

        exec.execute(() -> {
            User u = db.userDao().login(email, pass);
            runOnUiThread(() -> {
                if (u != null) {
                    session.createLoginSession(u.id);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}