package service;

import db.dao.ShipDAO;
import models.Ship;

public class ShipService {
    private final ShipDAO shipDAO = new ShipDAO();

    public void createShip(Ship ship) {
        shipDAO.createShip(ship);
    }

    public Ship getShipById(int id) {
        return shipDAO.getShipById(id);
    }

    public void updateShip(Ship ship) {
        shipDAO.updateShip(ship);
    }

    public void deleteShip(int id) {
        shipDAO.deleteShip(id);
    }
}
