package service;

import db.dao.GameDAO;
import db.dao.PlayerDAO;
import models.Game;
import models.GameStatus;
import models.Player;

public class GameService {
    private final GameDAO gameDAO = new GameDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();

    /**
     * Запускает новую игру между двумя игроками.
     *
     * @param player1 первый игрок
     * @param player2 второй игрок
     */
    public void startGame(Player player1, Player player2) {
        Game game = new Game(player1, player2);
        gameDAO.createGame(game);
        // Дополнительная логика: рассадка кораблей, подготовка игрового поля и т.д.
        System.out.println("Игра запущена с ID: " + game.getId());
    }

    /**
     * Завершает игру, обновляя статус и рейтинги игроков.
     * Победителю прибавляются 2 балла, у проигравшего отнимаются 2 балла.
     *
     * @param gameId    идентификатор игры
     * @param winnerId  идентификатор победителя
     */
    public void finishGame(int gameId, int winnerId) {
        Game game = gameDAO.getGameById(gameId);
        if (game == null) {
            System.out.println("Игра с таким ID не найдена.");
            return;
        }
        // Определяем победителя и проигравшего
        Player winner = playerDAO.getPlayerById(winnerId);
        Player loser;
        if (winner.getId() == game.getPlayer1().getId()) {
            loser = game.getPlayer2();
        } else {
            loser = game.getPlayer1();
        }
        // Обновляем статус игры и записываем победителя
        game.setWinner(winner);
        game.setStatus(GameStatus.FINISHED);
        gameDAO.updateGame(game);

        // Обновляем рейтинги и статистику
        winner.setRating(winner.getRating() + 2);
        loser.setRating(loser.getRating() - 2);
        winner.incrementWins();
        loser.incrementLosses();

        playerDAO.updatePlayer(winner);
        playerDAO.updatePlayer(loser);

        System.out.println("Игра завершена. Победитель: " + winner.getUsername());
    }
}
