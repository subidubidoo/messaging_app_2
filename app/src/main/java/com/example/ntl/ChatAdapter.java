package com.example.ntl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


// The adapter connects the chat list (from the database) with the item_chat design.
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(ChatMessage chat);
    }

    private List<ChatMessage> chats = new ArrayList<>();
    private final OnChatClickListener listener;

    public ChatAdapter(OnChatClickListener listener) {
        this.listener = listener;
    }

    // call this whenever the chats change
    public void setChats(List<ChatMessage> newChats) {
        chats = newChats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chat = chats.get(position);

        holder.name.setText(chat.getName());
        holder.message.setText(chat.getMessage());
        holder.date.setText(chat.getDate());
        holder.image.setImageResource(chat.getImage());

        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, message, date;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.profile_pic);
            name = itemView.findViewById(R.id.sender_name_tv);
            message = itemView.findViewById(R.id.messagePreviewTv);
            date = itemView.findViewById(R.id.datetv);
        }
    }
}
