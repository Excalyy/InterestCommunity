package com.example.interestcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameText, emailText, postsCount;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        postsCount = findViewById(R.id.postsCount);
        logoutButton = findViewById(R.id.logoutButton);

        // Кнопка назад в ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Профиль");
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Email всегда отображается
        emailText.setText("Email: " + (email != null ? email : "не загружен"));

        // Пока имя пользователя — просто "Пользователь" + часть email (можно потом сделать редактируемым)
        String username = "Пользователь " + email.split("@")[0];
        usernameText.setText("Имя пользователя: " + username);

        // Количество постов
        DatabaseHelper.getDatabaseReference().child("posts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot post : snapshot.getChildren()) {
                            if (post.child("userId").getValue(String.class).equals(currentUserId)) {
                                count++;
                            }
                        }
                        postsCount.setText("Количество постов: " + count);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        postsCount.setText("Ошибка загрузки");
                    }
                });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}