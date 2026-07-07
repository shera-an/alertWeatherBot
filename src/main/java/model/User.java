package model;

import java.time.LocalDateTime;

public class User {
    private String chatId;
    private String userName;
    private String firstName;
    private String city;
    private LocalDateTime registeredAt;

    public User(String chatId, String userName, String firstName, String city, LocalDateTime registeredAt) {
        this.chatId = chatId;
        this.userName = userName;
        this.firstName = firstName;
        this.city = city;
        this.registeredAt = registeredAt;
    }

    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
}