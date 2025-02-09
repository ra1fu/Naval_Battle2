package app;

import service.MenuService;
import service.PlayerService;
import service.TournamentService;
import service.TournamentGameService;
import service.BattleService;
import factory.PlayerFactory;
import models.Player;
import models.Role;
import models.Tournament;

import java.util.Scanner;

public class SeaBattleApp {
    private static final PlayerService playerService = new PlayerService();
    private static final TournamentService tournamentService = new TournamentService();
    private static final TournamentGameService tournamentGameService = new TournamentGameService();
    private static final MenuService menuService = new MenuService();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            menuService.printMainMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    registerPlayer(scanner);
                    break;
                case "2":
                    viewPlayerProfile(scanner);
                    break;
                case "3":
                    startBattleGame(scanner);
                    break;
                case "4":
                    createAdmin(scanner);
                    break;
                case "5":
                    tournamentMenu(scanner);
                    break;
                case "0":
                    exit = true;
                    System.out.println("Выход из приложения.");
                    break;
                default:
                    System.out.println("Некорректный выбор. Пожалуйста, попробуйте снова.");
            }
        }
        scanner.close();
    }

    private static void registerPlayer(Scanner scanner) {
        System.out.print("Введите имя игрока: ");
        String username = scanner.nextLine();

        Role role = (playerService.getAllPlayers().isEmpty()) ? Role.ADMIN : Role.PLAYER;
        playerService.registerPlayer(username, role);
    }

    private static void viewPlayerProfile(Scanner scanner) {
        System.out.print("Введите ID игрока: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Player player = playerService.getPlayerProfile(id);
            if (player != null) {
                System.out.println("Профиль игрока: " + player);
            } else {
                System.out.println("Игрок не найден.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ID.");
        }
    }

    private static void startBattleGame(Scanner scanner) {
        try {
            System.out.print("Введите ID первого игрока: ");
            int player1Id = Integer.parseInt(scanner.nextLine());
            System.out.print("Введите ID второго игрока: ");
            int player2Id = Integer.parseInt(scanner.nextLine());

            Player player1 = playerService.getPlayerProfile(player1Id);
            Player player2 = playerService.getPlayerProfile(player2Id);

            if (player1 == null || player2 == null) {
                System.out.println("Один из игроков не найден. Проверьте введённые ID.");
                return;
            }

            System.out.println("\nИгрок " + player1.getUsername() + ", разместите свои корабли.");
            BattleService bs1 = new BattleService();
            bs1.placeAllShipsManually(scanner);
            System.out.println("Поле игрока " + player1.getUsername() + " (для проверки):");
            bs1.printBoard(true);

            System.out.println("\nИгрок " + player2.getUsername() + ", разместите свои корабли.");
            BattleService bs2 = new BattleService();
            bs2.placeAllShipsManually(scanner);
            System.out.println("Поле игрока " + player2.getUsername() + " (для проверки):");
            bs2.printBoard(true);

            boolean player1Turn = true;
            while (true) {
                if (player1Turn) {
                    System.out.println("\nХод игрока " + player1.getUsername());
                    System.out.println("Поле противника:");
                    bs2.printBoard(false);
                    System.out.print("Введите координаты выстрела (например, А5): ");
                    String shot = scanner.nextLine().trim().toUpperCase();
                    String result = bs2.shoot(shot);
                    System.out.println("Результат: " + result);
                    if (bs2.isGameOver()) {
                        System.out.println("Поздравляем, " + player1.getUsername() + "! Вы выиграли.");
                        // Обновление рейтингов: победитель +2, проигравший -2
                        player1.setRating(player1.getRating() + 200);
                        player2.setRating(player2.getRating() - 200);
                        playerService.updatePlayer(player1);
                        playerService.updatePlayer(player2);
                        break;
                    }
                    if (result.equals("Miss") || result.equals("Already Shot") || result.equals("Invalid")) {
                        player1Turn = false;
                    }
                } else {
                    System.out.println("\nХод игрока " + player2.getUsername());
                    System.out.println("Поле противника:");
                    bs1.printBoard(false);
                    System.out.print("Введите координаты выстрела (например, А5): ");
                    String shot = scanner.nextLine().trim().toUpperCase();
                    String result = bs1.shoot(shot);
                    System.out.println("Результат: " + result);
                    if (bs1.isGameOver()) {
                        System.out.println("Поздравляем, " + player2.getUsername() + "! Вы выиграли.");
                        player2.setRating(player2.getRating() + 2);
                        player1.setRating(player1.getRating() - 2);
                        playerService.updatePlayer(player2);
                        playerService.updatePlayer(player1);
                        break;
                    }
                    if (result.equals("Miss") || result.equals("Already Shot") || result.equals("Invalid")) {
                        player1Turn = true;
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ввода ID.");
        }
    }

    private static void createAdmin(Scanner scanner) {
        System.out.print("Введите ID игрока, которого нужно сделать администратором: ");
        try {
            int playerId = Integer.parseInt(scanner.nextLine());
            playerService.makeAdmin(playerId);
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ID.");
        }
    }

    private static void tournamentMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            menuService.printTournamentMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    createTournament(scanner);
                    break;
                case "2":
                    registerPlayerToTournament(scanner);
                    break;
                case "3":
                    simulateTournament(scanner);
                    break;
                case "4":
                    back = true;
                    break;
                default:
                    System.out.println("Некорректный выбор. Повторите попытку.");
            }
        }
    }

    private static void createTournament(Scanner scanner) {
        System.out.print("Введите ID игрока (администратора): ");
        try {
            int playerId = Integer.parseInt(scanner.nextLine());
            Player player = playerService.getPlayerProfile(playerId);

            if (player != null) {
                System.out.print("Введите название турнира: ");
                String tournamentName = scanner.nextLine();
                tournamentService.createTournament(player, tournamentName);
            } else {
                System.out.println("Игрок не найден.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ID.");
        }
    }


    private static void registerPlayerToTournament(Scanner scanner) {
        try {
            System.out.print("Введите ID турнира: ");
            int tournamentId = Integer.parseInt(scanner.nextLine());
            Tournament tournament = tournamentService.getTournamentById(tournamentId);
            if (tournament == null) {
                System.out.println("Турнир с таким ID не найден.");
                return;
            }
            System.out.print("Введите ID игрока для регистрации: ");
            int playerId = Integer.parseInt(scanner.nextLine());
            Player player = playerService.getPlayerProfile(playerId);
            if (player == null) {
                System.out.println("Игрок с таким ID не найден.");
                return;
            }
            tournamentService.registerPlayerToTournament(player, tournament);
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат ввода ID.");
        }
    }



    private static void simulateTournament(Scanner scanner) {
        System.out.print("Введите ID турнира для симуляции: ");
        int tournamentId = Integer.parseInt(scanner.nextLine());
        Tournament tournament = tournamentService.getTournamentById(tournamentId);
        if (tournament == null) {
            System.out.println("Турнир с таким ID не найден.");
            return;
        }

        tournamentGameService.simulateTournament(tournament, scanner);
    }
}
