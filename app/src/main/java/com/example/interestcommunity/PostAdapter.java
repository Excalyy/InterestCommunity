package com.example.interestcommunity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.interestcommunity.models.Post;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private SimpleDateFormat dateFormat;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.authorName.setText(post.getAuthorName() != null ? post.getAuthorName() : "Аноним");

        if (post.getTimestamp() > 0) {
            holder.postTime.setText(dateFormat.format(new Date(post.getTimestamp())));
        } else {
            holder.postTime.setText("только что");
        }

        if (post.getCategory() != null && !post.getCategory().isEmpty()) {
            holder.postCategory.setText("Тема: " + post.getCategory());
            holder.postCategory.setVisibility(View.VISIBLE);
        } else {
            holder.postCategory.setVisibility(View.GONE);
        }

        holder.postTitle.setText(post.getTitle());
        holder.postContent.setText(post.getContent());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView authorName, postTime, postCategory, postTitle, postContent;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            authorName = itemView.findViewById(R.id.authorName);
            postTime = itemView.findViewById(R.id.postTime);
            postCategory = itemView.findViewById(R.id.postCategory);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
        }
    }
}