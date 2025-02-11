package db.dao;

import db.DBConnection;
import models.*;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class ShipDAO {
    private final Connection connection;

    public ShipDAO() {
        connection = DBConnection.getInstance().getConnection();
    }

    public void createShip(Ship ship) {
        String sql = "INSERT INTO ships (category, coordinates, is_sunk) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ship.getCategory().name());
            String coordinatesStr = String.join(",", ship.getCoordinates());
            ps.setString(2, coordinatesStr);
            ps.setBoolean(3, ship.isSunk());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ship.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при создании корабля: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Ship getShipById(int id) {
        String sql = "SELECT * FROM ships WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ship ship = new Ship();
                    ship.setId(rs.getInt("id"));
                    ship.setCategory(ShipCategory.valueOf(rs.getString("category")));
                    String coordinatesStr = rs.getString("coordinates");
                    List<String> coordinates = Arrays.asList(coordinatesStr.split(","));
                    ship.setCoordinates(coordinates);
                    ship.setSunk(rs.getBoolean("is_sunk"));
                    return ship;
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении корабля: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void updateShip(Ship ship) {
        String sql = "UPDATE ships SET category = ?, coordinates = ?, is_sunk = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, ship.getCategory().name());
            String coordinatesStr = String.join(",", ship.getCoordinates());
            ps.setString(2, coordinatesStr);
            ps.setBoolean(3, ship.isSunk());
            ps.setInt(4, ship.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении корабля: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteShip(int id) {
        String sql = "DELETE FROM ships WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении корабля: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
