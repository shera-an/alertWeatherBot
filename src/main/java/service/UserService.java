package service;

import config.DatabaseConfig;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.interfaces.UserStorage;

import java.sql.*;
import java.time.LocalDateTime;

public class UserService implements UserStorage {
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    @Override
    public void saveUser(User user) throws SQLException {
        logger.info("Сохраняем пользователя: {}, город: {}",user.getFirstName(), user.getCity());

        String sql = "INSERT INTO users (chat_id, username, first_name, city, registered_at) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (chat_id) DO UPDATE SET city = EXCLUDED.city";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getChatId());
            stmt.setString(2, user.getUserName());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getCity());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            int rows = stmt.executeUpdate();
            logger.info("Затронуто строк: {}", rows);
        } catch (SQLException e) {
            logger.error("Ошибка сохранения пользователя: ", e);
            throw e;
        }
    }
    @Override
    public User getUser(String chatId) throws SQLException {
        logger.info("Ищем пользователя с chatId: {}", chatId);

        String sql = "SELECT * FROM users WHERE chat_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chatId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getString("chat_id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("city"),
                        rs.getTimestamp("registered_at").toLocalDateTime()
                );
                logger.info("Пользователь найден: {}", user.getFirstName());
                return user;
            }
            logger.info("Пользователь не найден");

            return null;
        }
    }
    @Override
    public boolean userExists(String chatId) throws SQLException {
        return getUser(chatId) != null;
    }
}