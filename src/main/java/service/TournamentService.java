package service;

import db.dao.TournamentDAO;
import models.Player;
import models.Role;
import models.Tournament;

import java.util.*;


public class TournamentService {
    private final TournamentDAO tournamentDAO = new TournamentDAO();
    private final PlayerService playerService = new PlayerService();


    private final CustomSecurityManager securityManager = new CustomSecurityManager();

    public void createTournament(Player player, String tournamentName) {
        if (!securityManager.hasPermission(player, Role.ADMIN)) {
            return;
        }


        Tournament tournament = new Tournament(tournamentName);
        tournament.setStartDate(new Date());


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


    public void registerPlayerToTournament(Player player, Tournament tournament) {
        if (tournament.getParticipants().contains(player)) {
            System.out.println("Игрок " + player.getUsername() + " уже зарегистрирован в турнире.");
            return;
        }
        tournament.addParticipant(player);
        tournamentDAO.updateTournament(tournament);
        System.out.println("Игрок " + player.getUsername() + " успешно зарегистрирован в турнире " + tournament.getName());
    }


    public Map<Player, Integer> simulateTournamentGames(Tournament tournament) {
        List<Player> participants = tournament.getParticipants();
        Map<Player, Integer> scores = new HashMap<>();


        for (Player p : participants) {
            scores.put(p, 0);
        }


        for (int i = 0; i < participants.size(); i++) {
            for (int j = i + 1; j < participants.size(); j++) {
                Player p1 = participants.get(i);
                Player p2 = participants.get(j);

                Player winner = Math.random() < 0.5 ? p1 : p2;
                scores.put(winner, scores.get(winner) + 1);

                System.out.println("Матч: " + p1.getUsername() + " vs " + p2.getUsername() +
                        " — Победитель: " + winner.getUsername());
            }
        }
        return scores;
    }

    public List<Player> getPlayersInTournament(Tournament tournament) {
        return tournament.getParticipants();
    }


    public void updateRatingsBasedOnStandings(Tournament tournament, Map<Player, Integer> scores) {

        List<Map.Entry<Player, Integer>> ranking = new ArrayList<>(scores.entrySet());
        ranking.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        int bonus1 = 5;
        int bonus2 = 3;
        int bonus3 = 1;

        System.out.println("\nИтоговая турнирная таблица:");
        int rank = 1;
        for (Map.Entry<Player, Integer> entry : ranking) {
            Player player = entry.getKey();
            int score = entry.getValue();
            System.out.println(rank + ". " + player.getUsername() + " - " + score + " очков");

            if (rank == 1) {
                player.setRating(player.getRating() + bonus1);
            } else if (rank == 2) {
                player.setRating(player.getRating() + bonus2);
            } else if (rank == 3) {
                player.setRating(player.getRating() + bonus3);
            }

            playerService.updatePlayer(player);
            rank++;
        }
    }


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
