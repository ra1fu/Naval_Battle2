package service;

import models.Player;
import models.Role;

public class CustomSecurityManager {

    public boolean hasPermission(Player player, Role requiredRole) {
        if (player == null) {
            System.out.println("Ошибка: игрок не найден.");
            return false;
        }
        if (player.getRole() == requiredRole || player.getRole() == Role.ADMIN) {
            return true;
        }
        System.out.println("Ошибка: у вас нет прав для выполнения этой операции.");
        return false;
    }
}
