package com.example.interestcommunity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.interestcommunity.models.Post;

public class CreatePostActivity extends AppCompatActivity {

    private EditText titleEdit, contentEdit;
    private Spinner categorySpinner;
    private Button postButton;
    private FirebaseAuth auth;
    private DatabaseReference postsRef;

    // URL твоей базы — теперь всё будет работать!
    private static final String DATABASE_URL = "https://interestcommunity-f6a3f-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        titleEdit = findViewById(R.id.title);
        contentEdit = findViewById(R.id.content);
        categorySpinner = findViewById(R.id.spinner_category);
        postButton = findViewById(R.id.postButton);
        auth = FirebaseAuth.getInstance();

        // Инициализация базы с полным URL
        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        postsRef = database.getReference("posts");

        // Настройка Spinner для тем
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.post_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(0);

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
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Вы не авторизованы!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        String authorName = auth.getCurrentUser().getDisplayName();
        if (authorName == null || authorName.isEmpty()) {
            authorName = auth.getCurrentUser().getEmail().split("@")[0];
        }

        String category = categorySpinner.getSelectedItem().toString();

        Post post = new Post(title, content, userId);
        post.setAuthorName(authorName);
        post.setCategory(category);

        String postId = postsRef.push().getKey();
        if (postId == null) {
            Toast.makeText(this, "Ошибка генерации ID", Toast.LENGTH_SHORT).show();
            return;
        }

        post.setId(postId);

        postsRef.child(postId).setValue(post)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Пост опубликован!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}