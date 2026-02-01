package com.example.interestcommunity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static final String DATABASE_URL = "https://interestcommunity-f6a3f-default-rtdb.europe-west1.firebasedatabase.app/";

    private EditText etUsername;
    private TextView tvEmail, tvPostCount;
    private Button btnSave;

    private FirebaseAuth auth;
    private DatabaseReference userRef, postsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Профиль");
        }

        etUsername = findViewById(R.id.etUsername);
        tvEmail     = findViewById(R.id.userEmail);
        tvPostCount = findViewById(R.id.tvPostCount);
        btnSave     = findViewById(R.id.btnSaveProfile);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Не авторизован", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance(DATABASE_URL);
        userRef  = db.getReference("users").child(uid);
        postsRef = db.getReference("posts");  // для подсчёта постов

        loadProfileData();
        loadPostCount();

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfileData() {
        // Email из Auth
        String email = auth.getCurrentUser().getEmail();
        tvEmail.setText("Email: " + (email != null ? email : "не указан"));

        // Username из базы
        userRef.child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                if (username != null && !username.trim().isEmpty()) {
                    etUsername.setText(username);
                } else {
                    // Запасной вариант
                    String display = auth.getCurrentUser().getDisplayName();
                    etUsername.setText(display != null && !display.isEmpty() ? display : "Без имени");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Ошибка загрузки имени", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostCount() {
        // Подсчёт постов текущего пользователя
        postsRef.orderByChild("userId").equalTo(auth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        tvPostCount.setText("Количество постов: " + count);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        tvPostCount.setText("Количество постов: ошибка загрузки");
                    }
                });
    }

    private void saveProfile() {
        String newName = etUsername.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Имя не может быть пустым", Toast.LENGTH_SHORT).show();
            return;
        }

        // Обновляем Firebase Auth (displayName)
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build();

        auth.getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Обновляем в базе
                        userRef.child("username").setValue(newName)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(ProfileActivity.this, "Имя сохранено", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ProfileActivity.this, "Ошибка базы: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Ошибка Auth: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}