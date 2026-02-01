package com.example.interestcommunity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.interestcommunity.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String DATABASE_URL = "https://interestcommunity-f6a3f-default-rtdb.europe-west1.firebasedatabase.app/";
    private static final int POSTS_PER_PAGE = 4;

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> allPosts = new ArrayList<>();
    private List<Post> currentPagePosts = new ArrayList<>();

    private LinearLayout paginationContainer;
    private DatabaseReference postsRef;

    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.postsRecyclerView);
        paginationContainer = findViewById(R.id.paginationContainer); // ← добавь в xml

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(currentPagePosts);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance(DATABASE_URL);
        postsRef = database.getReference("posts");

        loadAllPosts();

        findViewById(R.id.createPostButton).setOnClickListener(v ->
                startActivity(new Intent(this, CreatePostActivity.class)));

        findViewById(R.id.profileButton).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void loadAllPosts() {
        postsRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allPosts.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post post = ds.getValue(Post.class);
                    if (post != null) {
                        post.setId(ds.getKey());
                        allPosts.add(0, post);
                    }
                }

                if (allPosts.isEmpty()) {
                    return;
                }

                totalPages = (int) Math.ceil((double) allPosts.size() / POSTS_PER_PAGE);
                updatePaginationButtons();
                showPage(currentPage);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void showPage(int page) {
        currentPage = page;
        currentPagePosts.clear();

        int start = (page - 1) * POSTS_PER_PAGE;
        int end = Math.min(start + POSTS_PER_PAGE, allPosts.size());

        for (int i = start; i < end; i++) {
            currentPagePosts.add(allPosts.get(i));
        }

        adapter.notifyDataSetChanged();
        updatePaginationButtons();
    }

    private void updatePaginationButtons() {
        paginationContainer.removeAllViews();

        for (int i = 1; i <= totalPages; i++) {
            Button btn = new Button(this);
            btn.setText(String.valueOf(i));
            btn.setWidth(120);
            btn.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            btn.setLayoutParams(params);

            final int page = i;
            btn.setOnClickListener(v -> showPage(page));

            if (i == currentPage) {
                btn.setBackgroundColor(0xFF2196F3);
                btn.setTextColor(0xFFFFFFFF);
            } else {
                btn.setBackgroundColor(0xFFE0E0E0);
            }

            paginationContainer.addView(btn);
        }
    }
}