package db.dao;

import db.DBConnection;
import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentDAO {
    private final Connection connection;

    public TournamentDAO() {
        connection = DBConnection.getInstance().getConnection();
    }

    /**
     * Создает турнир и добавляет участников.
     * @param tournament объект Tournament с заполненными полями и списком участников
     */
    public void createTournament(Tournament tournament) {
        // Сохранение основной информации о турнире
        String sqlTournament = "INSERT INTO tournaments (name, start_date) VALUES (?, ?)";
        try (PreparedStatement psTournament = connection.prepareStatement(sqlTournament, Statement.RETURN_GENERATED_KEYS)) {
            psTournament.setString(1, tournament.getName());
            psTournament.setDate(2, new java.sql.Date(tournament.getStartDate().getTime()));
            psTournament.executeUpdate();
            try (ResultSet rs = psTournament.getGeneratedKeys()) {
                if (rs.next()) {
                    tournament.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при создании турнира: " + e.getMessage());
            e.printStackTrace();
        }

        // Сохранение участников турнира
        String sqlParticipant = "INSERT INTO tournament_participants (tournament_id, player_id) VALUES (?, ?)";
        try (PreparedStatement psParticipant = connection.prepareStatement(sqlParticipant)) {
            for (Player player : tournament.getParticipants()) {
                psParticipant.setInt(1, tournament.getId());
                psParticipant.setInt(2, player.getId());
                psParticipant.addBatch();
            }
            psParticipant.executeBatch();
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении участников турнира: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Получает турнир по id вместе с участниками.
     * @param id идентификатор турнира
     * @return объект Tournament или null, если турнир не найден
     */
    public Tournament getTournamentById(int id) {
        Tournament tournament = null;
        String sqlTournament = "SELECT * FROM tournaments WHERE id = ?";
        try (PreparedStatement psTournament = connection.prepareStatement(sqlTournament)) {
            psTournament.setInt(1, id);
            try (ResultSet rs = psTournament.executeQuery()) {
                if (rs.next()) {
                    tournament = new Tournament();
                    tournament.setId(rs.getInt("id"));
                    tournament.setName(rs.getString("name"));
                    tournament.setStartDate(rs.getDate("start_date"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении турнира: " + e.getMessage());
            e.printStackTrace();
        }

        if (tournament != null) {
            List<Player> participants = new ArrayList<>();
            String sqlParticipants = "SELECT p.* FROM tournament_participants tp " +
                    "JOIN players p ON tp.player_id = p.id " +
                    "WHERE tp.tournament_id = ?";
            try (PreparedStatement psParticipants = connection.prepareStatement(sqlParticipants)) {
                psParticipants.setInt(1, tournament.getId());
                try (ResultSet rs = psParticipants.executeQuery()) {
                    while (rs.next()) {
                        Player player = new Player();
                        player.setId(rs.getInt("id"));
                        player.setUsername(rs.getString("username"));
                        player.setRating(rs.getInt("rating"));
                        player.setWins(rs.getInt("wins"));
                        player.setLosses(rs.getInt("losses"));
                        player.setRole(models.Role.valueOf(rs.getString("role")));
                        participants.add(player);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Ошибка при получении участников турнира: " + e.getMessage());
                e.printStackTrace();
            }
            tournament.setParticipants(participants);
        }
        return tournament;
    }

    /**
     * Обновляет данные турнира, включая участников.
     * @param tournament объект Tournament с обновленными данными
     */
    public void updateTournament(Tournament tournament) {
        // Обновляем основную информацию о турнире
        String sqlTournament = "UPDATE tournaments SET name = ?, start_date = ? WHERE id = ?";
        try (PreparedStatement psTournament = connection.prepareStatement(sqlTournament)) {
            psTournament.setString(1, tournament.getName());
            psTournament.setDate(2, new java.sql.Date(tournament.getStartDate().getTime()));
            psTournament.setInt(3, tournament.getId());
            psTournament.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении турнира: " + e.getMessage());
            e.printStackTrace();
        }

        // Для обновления участников можно удалить старые записи и добавить новые
        String sqlDeleteParticipants = "DELETE FROM tournament_participants WHERE tournament_id = ?";
        try (PreparedStatement psDelete = connection.prepareStatement(sqlDeleteParticipants)) {
            psDelete.setInt(1, tournament.getId());
            psDelete.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении участников турнира: " + e.getMessage());
            e.printStackTrace();
        }

        String sqlInsertParticipant = "INSERT INTO tournament_participants (tournament_id, player_id) VALUES (?, ?)";
        try (PreparedStatement psInsert = connection.prepareStatement(sqlInsertParticipant)) {
            for (Player player : tournament.getParticipants()) {
                psInsert.setInt(1, tournament.getId());
                psInsert.setInt(2, player.getId());
                psInsert.addBatch();
            }
            psInsert.executeBatch();
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении участников турнира: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Удаляет турнир и его участников по id.
     * @param id идентификатор турнира
     */
    public void deleteTournament(int id) {
        String sqlDeleteParticipants = "DELETE FROM tournament_participants WHERE tournament_id = ?";
        try (PreparedStatement psDelete = connection.prepareStatement(sqlDeleteParticipants)) {
            psDelete.setInt(1, id);
            psDelete.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении участников турнира: " + e.getMessage());
            e.printStackTrace();
        }
        String sqlTournament = "DELETE FROM tournaments WHERE id = ?";
        try (PreparedStatement psTournament = connection.prepareStatement(sqlTournament)) {
            psTournament.setInt(1, id);
            psTournament.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении турнира: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
