package util;

import models.Role;

public class DataValidator {
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Ошибка: Имя игрока не может быть пустым.");
            return false;
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            System.out.println("Ошибка: Имя игрока должно содержать только буквы, цифры и символы подчеркивания.");
            return false;
        }
        return true;
    }

    public static boolean isValidRole(String role) {
        try {
            Role.valueOf(role);
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: Некорректная роль. Должна быть роль 'PLAYER' или 'ADMIN'.");
            return false;
        }
    }

    public static boolean isValidRating(int rating) {
        if (rating < 0) {
            System.out.println("Ошибка: Рейтинг не может быть отрицательным.");
            return false;
        }
        return true;
    }

    public static boolean isValidWins(int wins) {
        if (wins < 0) {
            System.out.println("Ошибка: Количество побед не может быть отрицательным.");
            return false;
        }
        return true;
    }

    public static boolean isValidLosses(int losses) {
        if (losses < 0) {
            System.out.println("Ошибка: Количество поражений не может быть отрицательным.");
            return false;
        }
        return true;
    }

    public static boolean isValidTournamentName(String tournamentName) {
        if (tournamentName == null || tournamentName.trim().isEmpty()) {
            System.out.println("Ошибка: Название турнира не может быть пустым.");
            return false;
        }
        return true;
    }
}
