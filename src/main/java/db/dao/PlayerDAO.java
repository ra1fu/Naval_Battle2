package db.dao;

import db.DBConnection;
import models.Player;
import models.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {
    private final Connection connection;

    public PlayerDAO() {
        connection = DBConnection.getInstance().getConnection();
    }

    public void createPlayer(Player player) {
        String sql = "INSERT INTO players (username, rating, wins, losses, role) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, player.getUsername());
            ps.setInt(2, player.getRating());
            ps.setInt(3, player.getWins());
            ps.setInt(4, player.getLosses());
            ps.setString(5, player.getRole().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    player.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при создании игрока: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Player getPlayerById(int id) {
        String sql = "SELECT * FROM players WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    Player player = new Player();
                    player.setId(rs.getInt("id"));
                    player.setUsername(rs.getString("username"));
                    player.setRating(rs.getInt("rating"));
                    player.setWins(rs.getInt("wins"));
                    player.setLosses(rs.getInt("losses"));
                    player.setRole(Role.valueOf(rs.getString("role")));
                    return player;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении игрока: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setRating(rs.getInt("rating"));
                player.setWins(rs.getInt("wins"));
                player.setLosses(rs.getInt("losses"));
                player.setRole(Role.valueOf(rs.getString("role")));
                players.add(player);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка игроков: " + e.getMessage());
            e.printStackTrace();
        }
        return players;
    }


    public void updatePlayer(Player player) {
        String sql = "UPDATE players SET username = ?, rating = ?, wins = ?, losses = ?, role = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, player.getUsername());
            ps.setInt(2, player.getRating());
            ps.setInt(3, player.getWins());
            ps.setInt(4, player.getLosses());
            ps.setString(5, player.getRole().name());
            ps.setInt(6, player.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении данных игрока: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletePlayer(int id) {
        String sql = "DELETE FROM players WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении игрока: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
