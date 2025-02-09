package service;

import models.Player;
import models.Tournament;
import service.BattleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TournamentGameService {

    private final PlayerService playerService = new PlayerService();

    // Метод для симуляции турнира, где игроки делятся на пары и сражаются друг с другом.
    public void simulateTournament(Tournament tournament, Scanner scanner) {
        List<Player> players = tournament.getParticipants();

        // Проверка, что в турнире есть хотя бы два игрока
        if (players.size() < 2) {
            System.out.println("Для проведения турнира требуется минимум два игрока.");
            return;
        }

        // Разделяем игроков на пары и начинаем проводить матчи
        List<Player> winners = new ArrayList<>();
        while (players.size() > 1) {
            List<Player> roundWinners = new ArrayList<>();
            for (int i = 0; i < players.size(); i += 2) {
                if (i + 1 < players.size()) {
                    Player player1 = players.get(i);
                    Player player2 = players.get(i + 1);

                    System.out.println("\nСражение между " + player1.getUsername() + " и " + player2.getUsername());
                    BattleService bs1 = new BattleService();
                    BattleService bs2 = new BattleService();
                    bs1.placeAllShipsManually(scanner);
                    bs2.placeAllShipsManually(scanner);

                    boolean player1Turn = true;
                    while (true) {
                        if (player1Turn) {
                            System.out.print("Введите координаты выстрела: ");
                            String shot = scanner.nextLine();
                            bs2.shoot(shot);
                            if (bs2.isGameOver()) {
                                roundWinners.add(player1);
                                System.out.println(player1.getUsername() + " победил.");
                                break;
                            }
                            player1Turn = false;
                        } else {
                            System.out.print("Введите координаты выстрела: ");
                            String shot = scanner.nextLine();
                            bs1.shoot(shot);
                            if (bs1.isGameOver()) {
                                roundWinners.add(player2);
                                System.out.println(player2.getUsername() + " победил.");
                                break;
                            }
                            player1Turn = true;
                        }
                    }
                }
            }
            players = roundWinners;  // Обновляем список игроков для следующего раунда
        }

        // Победитель турнира
        Player tournamentWinner = players.get(0);
        System.out.println("Победитель турнира: " + tournamentWinner.getUsername());

        // Победитель получает бонусный рейтинг
        tournamentWinner.setRating(tournamentWinner.getRating() + 5);  // Увеличиваем рейтинг победителя на 5
        playerService.updatePlayer(tournamentWinner);

        // Выводим рейтинг победителя
        System.out.println("Рейтинг победителя после турнира: " + tournamentWinner.getRating());
    }
}
