package service;

import com.sun.source.tree.TryTree;
import config.DatabaseConfig;
import model.User;

import java.sql.*;
import java.time.LocalDateTime;

public class UserService {
    public void saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users (chat_id, username, first_name, city, registered_at) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,user.getChatId());
            stmt.setString(2, user.getUserName());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getCity());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            stmt.executeUpdate();
        }
    }
    public User getUser(String chatId) throws SQLException{
        String sql = "SELECT * FROM users WHERE chat_id = ?";
        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,chatId);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return  new User(
                        rs.getString("chat_id"),
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("city"),
                        rs.getTimestamp("registered_at").toLocalDateTime()
                );
            }
            return null;
        }
    }
    public void updateCity(String chatId, String city) throws SQLException{
        String sql = "UPDATE users SET city = ? WHERE chat_id = ?";

        try(Connection conn = DatabaseConfig.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1,city);
            stmt.setString(2,chatId);
            stmt.executeUpdate();
        }

    }
    public boolean userExists(String chatId) throws SQLException{
        return getUser(chatId) != null;
    }
}
