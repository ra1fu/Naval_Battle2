package models;

public class Game {
    private int id;
    private Player player1;
    private Player player2;
    private Player winner;      // Может быть null, если игра еще не завершена
    private GameStatus status;

    // Конструктор по умолчанию
    public Game() {
    }

    // Конструктор для создания новой игры
    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.status = GameStatus.ONGOING;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", winner=" + winner +
                ", status=" + status +
                '}';
    }
}
