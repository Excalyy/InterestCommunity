package com.example.interestcommunity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.interestcommunity.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class CreatePostActivity extends AppCompatActivity {

    private EditText titleEdit, contentEdit;
    private Button postButton;
    private FirebaseAuth auth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        titleEdit = findViewById(R.id.title);
        contentEdit = findViewById(R.id.content);
        postButton = findViewById(R.id.postButton);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Новый пост");
        }

        postButton.setOnClickListener(v -> createNewPost());
    }

    private void createNewPost() {
        String title = titleEdit.getText().toString().trim();
        String content = contentEdit.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_LONG).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Вы не авторизованы!", Toast.LENGTH_LONG).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        // Получаем имя пользователя из Firebase Auth (email или displayName)
        String userEmail = auth.getCurrentUser().getEmail();
        String displayName = auth.getCurrentUser().getDisplayName();
        String authorName = displayName != null && !displayName.isEmpty()
                ? displayName
                : (userEmail != null ? userEmail.split("@")[0] : "Аноним");

        // Создаем пост
        Post post = new Post(title, content, userId);
        post.setAuthorName(authorName);
        post.setTimestamp(System.currentTimeMillis());

        // Получаем ссылку на базу данных
        DatabaseReference postsRef = DatabaseHelper.getDatabaseReference().child("posts");
        String postId = postsRef.push().getKey();
        post.setId(postId);

        if (postId == null) {
            Toast.makeText(this, "Ошибка создания поста", Toast.LENGTH_LONG).show();
            return;
        }

        // Сохраняем пост в Firebase
        postsRef.child(postId).setValue(post)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Пост опубликован!", Toast.LENGTH_SHORT).show();
                    Log.d("CreatePost", "Пост создан: " + postId + " автором: " + authorName);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("CreatePost", "Ошибка создания поста", e);
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}