package com.app.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompanionAdapter extends RecyclerView.Adapter<CompanionAdapter.CompanionViewHolder> {
    private List<CompanionRequest> companionList;
    private Context context;

    public CompanionAdapter(Context context, List<CompanionRequest> companionList) {
        this.context = context;
        this.companionList = companionList;
    }

    @NonNull
    @Override
    public CompanionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.companion_request_item, parent, false);
        return new CompanionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanionViewHolder holder, int position) {
        CompanionRequest request = companionList.get(position);

        holder.date.setText(request.getDate());
        holder.time.setText(request.getTime());
        holder.location.setText(request.getLocation());
        holder.modeOfTravel.setText(request.getModeOfTravel());
        holder.userName.setText("User: " + request.getUserName());  // Display the user's name
        holder.buttonChat.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("receiverId", request.getUserId()); // send matched user's ID
            intent.putExtra("receiverName", request.getUserName()); // send matched user's name
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return companionList.size();
    }

    public static class CompanionViewHolder extends RecyclerView.ViewHolder {
        TextView date, time, location, modeOfTravel, userName;
        Button buttonChat;

        public CompanionViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textDate);
            time = itemView.findViewById(R.id.textTime);
            location = itemView.findViewById(R.id.textLocation);
            modeOfTravel = itemView.findViewById(R.id.textMode);
            userName = itemView.findViewById(R.id.companionUserName);  // New TextView for username
            buttonChat = itemView.findViewById(R.id.buttonChat);
        }
    }
}
