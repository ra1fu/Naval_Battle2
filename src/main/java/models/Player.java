package models;

public class Player {
    private int id;
    private String username;
    private int rating;
    private int wins;
    private int losses;
    private Role role;

    // Конструктор без параметров (может понадобиться при десериализации/ORM)
    public Player() {
    }

    // Основной конструктор для создания нового игрока
    public Player(String username, int rating, int wins, int losses, Role role) {
        this.username = username;
        this.rating = rating;
        this.wins = wins;
        this.losses = losses;
        this.role = role;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Дополнительные методы для обновления статистики
    public void incrementWins() {
        this.wins++;
    }

    public void incrementLosses() {
        this.losses++;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", rating=" + rating +
                ", wins=" + wins +
                ", losses=" + losses +
                ", role=" + role +
                '}';
    }
}
