package models;

import java.util.List;

public class Ship {
    private int id;
    private ShipCategory category;
    // Для простоты координаты будем хранить в виде списка строк (например, "A1", "B1", ...)
    // При необходимости можно создать отдельный класс Coordinate
    private List<String> coordinates;
    private boolean isSunk;

    // Конструктор по умолчанию
    public Ship() {
    }

    // Основной конструктор
    public Ship(ShipCategory category, List<String> coordinates) {
        this.category = category;
        this.coordinates = coordinates;
        this.isSunk = false;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ShipCategory getCategory() {
        return category;
    }

    public void setCategory(ShipCategory category) {
        this.category = category;
    }

    public List<String> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<String> coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isSunk() {
        return isSunk;
    }

    public void setSunk(boolean sunk) {
        isSunk = sunk;
    }

    /**
     * Метод для регистрации попадания в корабль.
     * Если все координаты подбиты, корабль считается потопленным.
     * Здесь можно реализовать более детальную логику.
     */
    public void hit(String coordinate) {
        // Пример: если координата содержится в списке, удалить её
        if (coordinates.remove(coordinate) && coordinates.isEmpty()) {
            isSunk = true;
        }
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", category=" + category +
                ", coordinates=" + coordinates +
                ", isSunk=" + isSunk +
                '}';
    }
}