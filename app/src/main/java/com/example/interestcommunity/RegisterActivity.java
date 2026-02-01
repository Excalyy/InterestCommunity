package com.example.interestcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String DATABASE_URL = "https://interestcommunity-f6a3f-default-rtdb.europe-west1.firebasedatabase.app/";

    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.email);
        etPassword = findViewById(R.id.password);

        Button registerButton = findViewById(R.id.registerButton);
        TextView loginText = findViewById(R.id.loginText);

        // Инициализация базы с правильным URL
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        usersRef = database.getReference("users");

        registerButton.setOnClickListener(v -> registerUser());

        loginText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен быть минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Успех — пользователь создан
                        String uid = mAuth.getCurrentUser().getUid();

                        // Сохраняем базовую информацию в базу
                        DatabaseReference userRef = usersRef.child(uid);
                        userRef.child("email").setValue(email);
                        userRef.child("username").setValue(email.split("@")[0]); // временное имя, можно потом редактировать

                        // Опционально: обновляем displayName в Auth
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(email.split("@")[0])
                                .build();
                        mAuth.getCurrentUser().updateProfile(profileUpdates);

                        Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();

                        // Переходим на главный экран
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();

                    } else {
                        // Показываем реальную ошибку от Firebase
                        Exception e = task.getException();
                        String errorMessage = "Ошибка регистрации";
                        if (e != null) {
                            errorMessage += ": " + e.getMessage();
                            Log.e("RegisterError", "Полная ошибка: ", e);
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }
}