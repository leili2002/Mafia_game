package chat.Repository;

import chat.DatabaseConnection.DatabaseConnection;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameRepository {

    // Add a new player to the game
    public void addPlayer(String name, String role) {
        String sql = "INSERT INTO players (name, role, is_alive) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, role);
            stmt.setBoolean(3, true);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
        }

    }

    // Update player's alive status
    public void updatePlayerStatus(String name, boolean isAlive) {
        String sql = "UPDATE players SET is_alive = ? WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isAlive);
            stmt.setString(2, name);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Assign role to a player
    public void assignRole(String name, String role) {
        String sql = "UPDATE players SET role = ? WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            stmt.setString(2, name);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all player names
    public List<String> getAllPlayerNames() {
        List<String> players = new ArrayList<>();
        String sql = "SELECT name FROM players";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                players.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    // Count alive players by role
    public int countAliveByRole(String role) {
        String sql = "SELECT COUNT(*) AS count FROM players WHERE role = ? AND is_alive = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Set winner of the game
    public void setWinner(String winner) {
        String sql = "UPDATE game SET winner = ? WHERE id = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, winner);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get role of a specific player
    public String getRole(String name) {
        String sql = "SELECT role FROM players WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
