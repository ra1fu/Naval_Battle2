package service;

import db.dao.GameDAO;
import db.dao.PlayerDAO;
import models.Game;
import models.GameStatus;
import models.Player;

public class GameService {
    private final GameDAO gameDAO = new GameDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();

    public void startGame(Player player1, Player player2) {
        Game game = new Game(player1, player2);
        gameDAO.createGame(game);
        System.out.println("Игра запущена с ID: " + game.getId());
    }
    public void finishGame(int gameId, int winnerId) {
        Game game = gameDAO.getGameById(gameId);
        if (game == null) {
            System.out.println("Игра с таким ID не найдена.");
            return;
        }
        Player winner = playerDAO.getPlayerById(winnerId);
        Player loser;
        if (winner.getId() == game.getPlayer1().getId()) {
            loser = game.getPlayer2();
        } else {
            loser = game.getPlayer1();
        }
        game.setWinner(winner);
        game.setStatus(GameStatus.FINISHED);
        gameDAO.updateGame(game);

        winner.setRating(winner.getRating() + 2);
        loser.setRating(loser.getRating() - 2);
        winner.incrementWins();
        loser.incrementLosses();

        playerDAO.updatePlayer(winner);
        playerDAO.updatePlayer(loser);

        System.out.println("Игра завершена. Победитель: " + winner.getUsername());
    }
}
