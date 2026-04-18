package com.example.demoapp;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPass, etConfirm;
    private AppDatabase db;
    private ExecutorService exec = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.et_reg_email);
        etPass = findViewById(R.id.et_reg_pass);
        etConfirm = findViewById(R.id.et_reg_confirm);
        db = AppDatabase.getInstance(this);

        Button btnReg = findViewById(R.id.btn_reg);
        btnReg.setOnClickListener(v -> register());
    }

    private void register() {
        String email = etEmail.getText().toString().trim();
        String pass = etPass.getText().toString().trim();
        String conf = etConfirm.getText().toString().trim();

        // Валидация в UI-потоке
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Некорректный Email");
            return;
        }
        if (pass.length() < 6) {
            etPass.setError("Минимум 6 символов");
            return;
        }
        if (!pass.equals(conf)) {
            etConfirm.setError("Пароли не совпадают");
            return;
        }


        exec.execute(() -> {
            User exists = db.userDao().getUserByEmail(email);

            runOnUiThread(() -> {
                if (exists != null) {
                    Toast.makeText(this, "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                    return;
                }


                User u = new User();
                u.email = email;
                u.password = pass;


                exec.execute(() -> {
                    db.userDao().insert(u);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Успешная регистрация", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            });
        });
    }
}