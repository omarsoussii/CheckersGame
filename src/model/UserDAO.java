package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import utils.ConnexionDB;


public class UserDAO {
    public boolean inscrire(User user) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("error: " + e.getMessage());
            return false;
        }
    }

    public User login(String email, String password) {
        String sql = "SELECT username, password, email FROM users WHERE email = ? AND password = ?";
        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            System.err.println("login error: " + e.getMessage());
        }
        return null;
    }

    public boolean updateStats(User user) {
        String userSQL = "UPDATE users SET total_games = ?, wins = ?, losses = ? WHERE username = ?";
        String statsSQL = "INSERT INTO user_difficulty_stats (username, difficulty, game_count) " +
                         "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE game_count = ?";

        try (Connection conn = ConnexionDB.getConnexion()) {
            conn.setAutoCommit(false);
            try (PreparedStatement userStmt = conn.prepareStatement(userSQL);
                 PreparedStatement statsStmt = conn.prepareStatement(statsSQL)) {
                userStmt.setInt(1, user.getTotalGames());
                userStmt.setInt(2, user.getWins());
                userStmt.setInt(3, user.getLosses());
                userStmt.setString(4, user.getUsername());
                userStmt.executeUpdate();
                for (Map.Entry<AIDifficulty, Integer> entry : user.getGamesByDifficulty().entrySet()) {
                    statsStmt.setString(1, user.getUsername());
                    statsStmt.setString(2, entry.getKey().name());
                    statsStmt.setInt(3, entry.getValue());
                    statsStmt.setInt(4, entry.getValue());
                    statsStmt.executeUpdate();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Stats update error: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("db connection error: " + e.getMessage());
            return false;
        }
    }

    public boolean userExists(String username, String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ? OR email = ?";
        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User getUserStats(String username) {
        String userSQL = "SELECT username, email, password, total_games, wins, losses FROM users WHERE username = ?";
        String statsSQL = "SELECT difficulty, game_count FROM user_difficulty_stats WHERE username = ?";

        try (Connection conn = ConnexionDB.getConnexion();
             PreparedStatement userStmt = conn.prepareStatement(userSQL)) {
            userStmt.setString(1, username);
            try (ResultSet userRs = userStmt.executeQuery()) {
                if (!userRs.next()) return null;

                User user = new User(userRs.getString("username"), userRs.getString("password"), userRs.getString("email"));
                user.setTotalGames(userRs.getInt("total_games"));
                user.setWins(userRs.getInt("wins"));
                user.setLosses(userRs.getInt("losses"));

                try (PreparedStatement statsStmt = conn.prepareStatement(statsSQL)) {
                    statsStmt.setString(1, username);
                    try (ResultSet statsRs = statsStmt.executeQuery()) {
                        while (statsRs.next()) {
                            user.setGamesByDifficulty(AIDifficulty.valueOf(statsRs.getString("difficulty")), statsRs.getInt("game_count"));
                        }
                    }
                }
                return user;
            }
        } catch (SQLException e) {
            System.err.println("User stats error: " + e.getMessage());
            return null;
        }
    }
}