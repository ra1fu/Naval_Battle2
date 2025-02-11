package service;

import models.Role;
import models.Player;

public class SecurityManager {


    public boolean hasPermission(Player player, Role requiredRole) {
        if (player == null) {
            System.out.println("Ошибка: игрок не найден.");
            return false;
        }

        if (player.getRole() == requiredRole) {
            return true;
        }

        System.out.println("Ошибка: у вас нет прав для выполнения этой операции.");
        return false;
    }
}
