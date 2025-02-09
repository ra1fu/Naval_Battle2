package service;

import db.dao.TournamentDAO;
import models.Player;
import models.Role;
import models.Tournament;

import java.util.*;

/**
 * Сервис для работы с турнирами.
 * Помимо базовых операций (создание, получение, обновление, удаление),
 * реализует регистрацию участников, симуляцию матчей и обновление рейтингов по итогам турнира.
 */
public class TournamentService {
    private final TournamentDAO tournamentDAO = new TournamentDAO();
    private final PlayerService playerService = new PlayerService(); // для обновления рейтингов игроков


    private final CustomSecurityManager securityManager = new CustomSecurityManager();

    public void createTournament(Player player, String tournamentName) {
        // Проверка прав доступа
        if (!securityManager.hasPermission(player, Role.ADMIN)) {
            return;  // Если у игрока нет прав, выходим из метода
        }

        // Создаем турнир, если права подтверждены
        Tournament tournament = new Tournament(tournamentName);
        tournament.setStartDate(new Date());

        // Сохраняем турнир через DAO
        tournamentDAO.createTournament(tournament);
        System.out.println("Турнир создан: " + tournamentName);
    }

    public Tournament getTournamentById(int id) {
        return tournamentDAO.getTournamentById(id);
    }

    public void updateTournament(Tournament tournament) {
        tournamentDAO.updateTournament(tournament);
    }

    public void deleteTournament(int id) {
        tournamentDAO.deleteTournament(id);
    }

    // Расширенная логика турниров:

    /**
     * Регистрирует игрока в турнире.
     *
     * @param player     объект игрока, который регистрируется
     * @param tournament турнир, в который регистрируется игрок
     */
    public void registerPlayerToTournament(Player player, Tournament tournament) {
        if (tournament.getParticipants().contains(player)) {
            System.out.println("Игрок " + player.getUsername() + " уже зарегистрирован в турнире.");
            return;
        }
        tournament.addParticipant(player);
        tournamentDAO.updateTournament(tournament);
        System.out.println("Игрок " + player.getUsername() + " успешно зарегистрирован в турнире " + tournament.getName());
    }

    /**
     * Симулирует игры турнира по системе round-robin (каждый играет с каждым)
     * и подсчитывает очки за победы.
     *
     * @param tournament турнир, в котором будут проводиться матчи
     * @return карту, где ключ — игрок, а значение — набранное количество очков
     */
    public Map<Player, Integer> simulateTournamentGames(Tournament tournament) {
        List<Player> participants = tournament.getParticipants();
        Map<Player, Integer> scores = new HashMap<>();

        // Инициализируем счет каждого участника нулем
        for (Player p : participants) {
            scores.put(p, 0);
        }

        // Для каждого уникального противостояния (round-robin)
        for (int i = 0; i < participants.size(); i++) {
            for (int j = i + 1; j < participants.size(); j++) {
                Player p1 = participants.get(i);
                Player p2 = participants.get(j);
                // Симуляция игры: случайным образом определяем победителя
                Player winner = Math.random() < 0.5 ? p1 : p2;
                scores.put(winner, scores.get(winner) + 1);
                // Можно добавить вывод результата матча:
                System.out.println("Матч: " + p1.getUsername() + " vs " + p2.getUsername() +
                        " — Победитель: " + winner.getUsername());
            }
        }
        return scores;
    }

    public List<Player> getPlayersInTournament(Tournament tournament) {
        return tournament.getParticipants();
    }

    /**
     * Обновляет рейтинговые баллы участников турнира на основе итоговых позиций.
     * Для примера: первый получает +5, второй +3, третий +1 балл.
     *
     * @param tournament турнир, по результатам которого обновляются рейтинги
     * @param scores     карта с очками участников турнира
     */
    public void updateRatingsBasedOnStandings(Tournament tournament, Map<Player, Integer> scores) {
        // Сортируем участников по количеству очков (по убыванию)
        List<Map.Entry<Player, Integer>> ranking = new ArrayList<>(scores.entrySet());
        ranking.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Определяем бонусы для первых трех позиций
        int bonus1 = 5;
        int bonus2 = 3;
        int bonus3 = 1;

        System.out.println("\nИтоговая турнирная таблица:");
        int rank = 1;
        for (Map.Entry<Player, Integer> entry : ranking) {
            Player player = entry.getKey();
            int score = entry.getValue();
            System.out.println(rank + ". " + player.getUsername() + " - " + score + " очков");
            // Присваиваем бонусы лидерам турнира
            if (rank == 1) {
                player.setRating(player.getRating() + bonus1);
            } else if (rank == 2) {
                player.setRating(player.getRating() + bonus2);
            } else if (rank == 3) {
                player.setRating(player.getRating() + bonus3);
            }
            // Обновляем профиль игрока в БД
            playerService.updatePlayer(player);
            rank++;
        }
    }

    /**
     * Выводит турнирную таблицу на экран.
     *
     * @param scores карта с очками участников турнира
     */
    public void displayTournamentStandings(Map<Player, Integer> scores) {
        List<Map.Entry<Player, Integer>> ranking = new ArrayList<>(scores.entrySet());
        ranking.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        System.out.println("\nТурнирная таблица:");
        int rank = 1;
        for (Map.Entry<Player, Integer> entry : ranking) {
            System.out.println(rank + ". " + entry.getKey().getUsername() + " - " + entry.getValue() + " очков");
            rank++;
        }
    }
}
