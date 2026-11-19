package com.example.ntl;
public class ChatMessage {

    private int chatId;
    private String name;
    private String message;
    private String date;
    private int image;

    public ChatMessage(int chatId, String name, String message, String date, int image) {
        this.chatId = chatId;
        this.name = name;
        this.message = message;
        this.date = date;
        this.image = image;
    }

    public int getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public int getImage() {
        return image;
    }
}
