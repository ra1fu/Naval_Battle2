package services;

import db.dao.PlayerDAO;
import models.Player;
import models.Role;

import java.util.List;

public class PlayerService {

    private final PlayerDAO playerDAO = new PlayerDAO();

    public void registerPlayer(String username, Role role) {
        Player newPlayer = new Player();
        newPlayer.setUsername(username);
        newPlayer.setRole(role);
        newPlayer.setRating(1000);
        newPlayer.setWins(0);
        newPlayer.setLosses(0);
        playerDAO.createPlayer(newPlayer);
        System.out.println("Игрок зарегистрирован: " + newPlayer);
    }

    public Player getPlayerProfile(int id) {
        return playerDAO.getPlayerById(id);
    }

    public void updatePlayer(Player player) {
        playerDAO.updatePlayer(player);
    }

    public List<Player> getAllPlayers() {
        return playerDAO.getAllPlayers();
    }

    public void makeAdmin(int playerId) {
        Player player = playerDAO.getPlayerById(playerId);
        if (player != null) {
            player.setRole(Role.ADMIN);
            playerDAO.updatePlayer(player);
            System.out.println("Игрок " + player.getUsername() + " теперь является администратором.");
        } else {
            System.out.println("Игрок с таким ID не найден.");
        }
    }
}
