package com.example.ntl;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ntl.database.UsersDatabase;
import com.example.ntl.modules.Chats;
import com.example.ntl.modules.Messages;
import com.example.ntl.modules.Users;

public class Homepage extends AppCompatActivity {
    RecyclerView messagesRecyclerView;
    ChatAdapter chatAdapter;
    UsersDatabase db;
    int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = UsersDatabase.getInstance(this);

        // who is logged in (saved by Signin)
        currentUserId = getSharedPreferences("session", MODE_PRIVATE).getInt("userId", -1);
        if (currentUserId == -1) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        messagesRecyclerView = findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // tapping a chat opens it
        chatAdapter = new ChatAdapter(chat -> {
            Intent intent = new Intent(this, ChatRoom.class);
            intent.putExtra("chatId", chat.getChatId());
            intent.putExtra("otherName", chat.getName());
            startActivity(intent);
        });
        messagesRecyclerView.setAdapter(chatAdapter);

        loadChats();
    }

    private void loadChats() {
        UsersDatabase.databaseExecutor.execute(() -> {
            // accounts created before this feature may not have a self chat yet
            if (db.chatDao().getChatBetween(currentUserId, currentUserId) == null) {
                Chats selfChat = new Chats();
                selfChat.user1Id = currentUserId;
                selfChat.user2Id = currentUserId;
                selfChat.lastMessageSent = System.currentTimeMillis();
                db.chatDao().insertChat(selfChat);
            }

            // LiveData must be observed on the main thread
            runOnUiThread(() ->
                    db.chatDao().getChatsForUser(currentUserId).observe(this, this::showChats));
        });
    }

    // turns database rows into list items (needs the database, so not on the main thread)
    private void showChats(List<Chats> chats) {
        UsersDatabase.databaseExecutor.execute(() -> {
            List<ChatMessage> items = new ArrayList<>();

            for (Chats chat : chats) {
                int otherId = (chat.user1Id == currentUserId) ? chat.user2Id : chat.user1Id;
                boolean isSelfChat = (otherId == currentUserId);

                String name;
                if (isSelfChat) {
                    name = "Me";
                } else {
                    Users other = db.usersDao().getUserById(otherId);
                    name = (other != null) ? other.userName : "Unknown";
                }

                Messages last = db.messageDao().getLastMessage(chat.chatId);
                String preview = (last != null) ? last.message_text : "No messages yet";
                String date = DateUtils.getRelativeTimeSpanString(
                        chat.lastMessageSent,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS).toString();

                items.add(new ChatMessage(chat.chatId, name, preview, date,
                        isSelfChat ? R.drawable.pfp : R.drawable.pfp));
            }

            runOnUiThread(() -> chatAdapter.setChats(items));
        });
    }
}
