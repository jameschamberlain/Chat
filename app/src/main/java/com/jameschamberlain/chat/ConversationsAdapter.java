package com.jameschamberlain.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    private ArrayList<String> users;
    private AppCompatActivity parentActivity;

    public ConversationsAdapter(ArrayList<String> users, AppCompatActivity activity) {
        this.users = users;
        this.parentActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentUser = users.get(position);
        holder.name.setText(currentUser);
        holder.parentLayout.setOnClickListener(new UserOnClickListener(currentUser, parentActivity));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        LinearLayout parentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
