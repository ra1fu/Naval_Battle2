package service;

import db.dao.ShipDAO;
import models.Ship;

public class ShipService {
    private final ShipDAO shipDAO = new ShipDAO();

    /**
     * Создаёт новый корабль.
     *
     * @param ship объект корабля
     */
    public void createShip(Ship ship) {
        shipDAO.createShip(ship);
    }

    /**
     * Получает корабль по идентификатору.
     *
     * @param id идентификатор корабля
     * @return объект Ship или null, если корабль не найден
     */
    public Ship getShipById(int id) {
        return shipDAO.getShipById(id);
    }

    /**
     * Обновляет данные корабля.
     *
     * @param ship объект Ship с обновлёнными данными
     */
    public void updateShip(Ship ship) {
        shipDAO.updateShip(ship);
    }

    /**
     * Удаляет корабль по идентификатору.
     *
     * @param id идентификатор корабля
     */
    public void deleteShip(int id) {
        shipDAO.deleteShip(id);
    }
}
