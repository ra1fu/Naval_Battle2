package db.dao;

import db.DBConnection;
import models.*;

import java.sql.*;

public class GameDAO {
    private final Connection connection;

    public GameDAO() {
        connection = DBConnection.getInstance().getConnection();
    }


    public void createGame(Game game) {
        String sql = "INSERT INTO games (player1_id, player2_id, winner_id, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, game.getPlayer1().getId());
            ps.setInt(2, game.getPlayer2().getId());
            if (game.getWinner() != null) {
                ps.setInt(3, game.getWinner().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, game.getStatus().name());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    game.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при создании игры: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Game getGameById(int id) {
        String sql = "SELECT g.id, g.status, " +
                "p1.id AS p1_id, p1.username AS p1_username, p1.rating AS p1_rating, p1.wins AS p1_wins, p1.losses AS p1_losses, p1.role AS p1_role, " +
                "p2.id AS p2_id, p2.username AS p2_username, p2.rating AS p2_rating, p2.wins AS p2_wins, p2.losses AS p2_losses, p2.role AS p2_role, " +
                "p3.id AS winner_id, p3.username AS winner_username, p3.rating AS winner_rating, p3.wins AS winner_wins, p3.losses AS winner_losses, p3.role AS winner_role " +
                "FROM games g " +
                "JOIN players p1 ON g.player1_id = p1.id " +
                "JOIN players p2 ON g.player2_id = p2.id " +
                "LEFT JOIN players p3 ON g.winner_id = p3.id " +
                "WHERE g.id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Player player1 = new Player();
                    player1.setId(rs.getInt("p1_id"));
                    player1.setUsername(rs.getString("p1_username"));
                    player1.setRating(rs.getInt("p1_rating"));
                    player1.setWins(rs.getInt("p1_wins"));
                    player1.setLosses(rs.getInt("p1_losses"));
                    player1.setRole(Role.valueOf(rs.getString("p1_role")));

                    Player player2 = new Player();
                    player2.setId(rs.getInt("p2_id"));
                    player2.setUsername(rs.getString("p2_username"));
                    player2.setRating(rs.getInt("p2_rating"));
                    player2.setWins(rs.getInt("p2_wins"));
                    player2.setLosses(rs.getInt("p2_losses"));
                    player2.setRole(Role.valueOf(rs.getString("p2_role")));

                    Player winner = null;
                    int winnerId = rs.getInt("winner_id");
                    if (!rs.wasNull()) {
                        winner = new Player();
                        winner.setId(winnerId);
                        winner.setUsername(rs.getString("winner_username"));
                        winner.setRating(rs.getInt("winner_rating"));
                        winner.setWins(rs.getInt("winner_wins"));
                        winner.setLosses(rs.getInt("winner_losses"));
                        winner.setRole(Role.valueOf(rs.getString("winner_role")));
                    }

                    Game game = new Game();
                    game.setId(rs.getInt("id"));
                    game.setPlayer1(player1);
                    game.setPlayer2(player2);
                    game.setWinner(winner);
                    game.setStatus(GameStatus.valueOf(rs.getString("status")));

                    return game;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении игры: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void updateGame(Game game) {
        String sql = "UPDATE games SET player1_id = ?, player2_id = ?, winner_id = ?, status = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, game.getPlayer1().getId());
            ps.setInt(2, game.getPlayer2().getId());
            if (game.getWinner() != null) {
                ps.setInt(3, game.getWinner().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, game.getStatus().name());
            ps.setInt(5, game.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении игры: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteGame(int id) {
        String sql = "DELETE FROM games WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении игры: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
