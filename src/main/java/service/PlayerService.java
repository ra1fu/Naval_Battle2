package service;

import db.dao.PlayerDAO;
import models.Player;
import models.Role;

import java.util.List;

public class PlayerService {

    private final PlayerDAO playerDAO = new PlayerDAO();  // Используем PlayerDAO для работы с БД

    // Метод для регистрации нового игрока
    public void registerPlayer(String username, Role role) {
        Player newPlayer = new Player();
        newPlayer.setUsername(username);
        newPlayer.setRole(role);  // Роль PLAYER или ADMIN
        newPlayer.setRating(1000);  // Пример начального рейтинга
        newPlayer.setWins(0);
        newPlayer.setLosses(0);
        playerDAO.createPlayer(newPlayer);  // Сохраняем игрока в БД
        System.out.println("Игрок зарегистрирован: " + newPlayer);
    }

    // Метод для получения профиля игрока по ID
    public Player getPlayerProfile(int id) {
        return playerDAO.getPlayerById(id);  // Получаем игрока из БД
    }

    // Метод для обновления данных игрока
    public void updatePlayer(Player player) {
        playerDAO.updatePlayer(player);  // Обновляем данные игрока в БД
    }

    // Метод для получения всех игроков
    public List<Player> getAllPlayers() {
        return playerDAO.getAllPlayers();  // Получаем всех игроков из БД
    }

    // Метод для назначения игрока администратором
    public void makeAdmin(int playerId) {
        Player player = playerDAO.getPlayerById(playerId);  // Получаем игрока из БД
        if (player != null) {
            player.setRole(Role.ADMIN);  // Меняем роль игрока на администратора
            playerDAO.updatePlayer(player);  // Обновляем игрока в БД
            System.out.println("Игрок " + player.getUsername() + " теперь является администратором.");
        } else {
            System.out.println("Игрок с таким ID не найден.");
        }
    }
}
