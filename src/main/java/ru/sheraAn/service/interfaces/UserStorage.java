package ru.sheraAn.service.interfaces;

import ru.sheraAn.model.User;

import java.sql.SQLException;

public interface UserStorage {
    void saveUser(User user) throws SQLException;
    User getUser(String chatId) throws SQLException;
    boolean userExists(String chatId) throws SQLException;
}
