package service;

import java.util.Scanner;

public class MenuService {

    public void printMainMenu() {
        System.out.println("\n=== Sea Battle App ===");
        System.out.println("1. Зарегистрировать игрока");
        System.out.println("2. Просмотр профиля игрока");
        System.out.println("3. Начать новую игру (двух игроков)");
        System.out.println("4. Назначить Админа");
        System.out.println("5. Меню Турниров");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    public void printTournamentMenu() {
        System.out.println("\n=== Меню турниров ===");
        System.out.println("1. Создать турнир");
        System.out.println("2. Зарегистрировать игрока в турнире");
        System.out.println("3. Симулировать турнир и обновить рейтинги");
        System.out.println("4. Назад в главное меню");
        System.out.print("Выберите действие: ");
    }
}
