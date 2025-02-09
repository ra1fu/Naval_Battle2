package factory;

import models.Player;
import models.Role;
import util.DataValidator;

public class PlayerFactory {
    /**
     * Создаёт нового игрока с проверкой корректности имени.
     *
     * @param username имя пользователя
     * @param role     роль игрока
     * @return новый объект Player
     */
    public static Player createPlayer(String username, Role role) {
        if (!DataValidator.isValidUsername(username)) {
            throw new IllegalArgumentException("Некорректное имя пользователя");
        }
        int initialRating = 1000;
        return new Player(username, initialRating, 0, 0, role);
    }
}
