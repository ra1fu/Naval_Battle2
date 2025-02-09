package services;

import models.Ship;
import models.ShipCategory;

import java.util.*;


public class BattleService {
    public static final int BOARD_SIZE = 10;

    public enum CellState {
        EMPTY,
        SHIP,
        HIT,
        MISS
    }

    private CellState[][] board;
    private List<Ship> ships;
    private final int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    private final String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    public BattleService() {
        board = new CellState[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            Arrays.fill(board[i], CellState.EMPTY);
        }
        ships = new ArrayList<>();
    }

    public void placeAllShipsManually(Scanner scanner) {
        System.out.println("Ручное размещение кораблей. Игроку необходимо разместить следующие корабли:");
        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                printBoard(true);
                System.out.println("Разместите корабль размером " + size + " палуб(ы).");
                System.out.print("Введите начальную координату (например, А5): ");
                String coordInput = scanner.nextLine().trim().toUpperCase();
                int[] indices = convertFromCoordinate(coordInput);
                int row = indices[0];
                int col = indices[1];
                if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
                    System.out.println("Неверная координата. Попробуйте ещё раз.");
                    continue;
                }
                System.out.print("Введите ориентацию (H для горизонтальной, V для вертикальной): ");
                String orientInput = scanner.nextLine().trim().toUpperCase();
                int orientation;
                if (orientInput.equals("H")) {
                    orientation = 0;
                } else if (orientInput.equals("V")) {
                    orientation = 1;
                } else {
                    System.out.println("Неверная ориентация. Попробуйте ещё раз.");
                    continue;
                }

                if (orientation == 0 && col + size > BOARD_SIZE) {
                    System.out.println("Корабль выходит за пределы поля. Попробуйте другую позицию.");
                    continue;
                }
                if (orientation == 1 && row + size > BOARD_SIZE) {
                    System.out.println("Корабль выходит за пределы поля. Попробуйте другую позицию.");
                    continue;
                }

                if (!isValidPlacement(row, col, orientation, size)) {
                    System.out.println("Неверное размещение: корабли не должны соприкасаться с другими. Попробуйте ещё раз.");
                    continue;
                }

                List<String> shipCoords = new ArrayList<>();
                if (orientation == 0) {
                    for (int j = col; j < col + size; j++) {
                        board[row][j] = CellState.SHIP;
                        shipCoords.add(convertToCoordinate(row, j));
                    }
                } else {
                    for (int i = row; i < row + size; i++) {
                        board[i][col] = CellState.SHIP;
                        shipCoords.add(convertToCoordinate(i, col));
                    }
                }
                Ship ship;
                if (size == 1) {
                    ship = new Ship(ShipCategory.LIGHT, shipCoords);
                } else if (size == 2) {
                    ship = new Ship(ShipCategory.MEDIUM, shipCoords);
                } else {
                    ship = new Ship(ShipCategory.HEAVY, shipCoords);
                }
                ships.add(ship);
                placed = true;
            }
        }
        System.out.println("Размещение кораблей завершено.");
    }

    private boolean isValidPlacement(int row, int col, int orientation, int size) {
        List<int[]> indices = new ArrayList<>();
        if (orientation == 0) {
            for (int j = col; j < col + size; j++) {
                indices.add(new int[]{row, j});
            }
        } else {
            for (int i = row; i < row + size; i++) {
                indices.add(new int[]{i, col});
            }
        }
        for (int[] index : indices) {
            int r = index[0];
            int c = index[1];
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr;
                    int nc = c + dc;
                    if (nr >= 0 && nr < BOARD_SIZE && nc >= 0 && nc < BOARD_SIZE) {
                        if (board[nr][nc] == CellState.SHIP) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private String convertToCoordinate(int row, int col) {
        return letters[col] + (row + 1);
    }

    private int[] convertFromCoordinate(String coordinate) {
        coordinate = coordinate.toUpperCase().trim();
        if (coordinate.length() < 2) return new int[]{-1, -1};
        char letterChar = coordinate.charAt(0);
        int col = -1;
        for (int j = 0; j < letters.length; j++) {
            if (letters[j].equals(String.valueOf(letterChar))) {
                col = j;
                break;
            }
        }
        int row;
        try {
            row = Integer.parseInt(coordinate.substring(1)) - 1;
        } catch (NumberFormatException e) {
            row = -1;
        }
        return new int[]{row, col};
    }

    public String shoot(String coordStr) {
        int[] indices = convertFromCoordinate(coordStr);
        int row = indices[0];
        int col = indices[1];
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            return "Invalid";
        }
        if (board[row][col] == CellState.HIT || board[row][col] == CellState.MISS) {
            return "Already Shot";
        }
        if (board[row][col] == CellState.SHIP) {
            board[row][col] = CellState.HIT;
            String targetCoord = convertToCoordinate(row, col);
            for (Ship ship : ships) {
                if (ship.getCoordinates().contains(targetCoord)) {
                    ship.hit(targetCoord);
                    if (ship.isSunk()) {
                        markAdjacentAsMiss(ship);
                        return "Sunk";
                    }
                    return "Hit";
                }
            }
            return "Hit";
        } else { // EMPTY
            board[row][col] = CellState.MISS;
            return "Miss";
        }
    }

    private void markAdjacentAsMiss(Ship ship) {
        for (String coordStr : ship.getCoordinates()) {
            int[] indices = convertFromCoordinate(coordStr);
            int row = indices[0];
            int col = indices[1];
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int r = row + dr;
                    int c = col + dc;
                    if (r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE) {
                        if (board[r][c] == CellState.EMPTY) {
                            board[r][c] = CellState.MISS;
                        }
                    }
                }
            }
        }
    }

    public boolean isGameOver() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    public void printBoard(boolean revealShips) {
        System.out.print("   ");
        for (int j = 0; j < BOARD_SIZE; j++) {
            System.out.print(" " + letters[j]);
        }
        System.out.println();
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.printf("%2d ", i + 1);
            for (int j = 0; j < BOARD_SIZE; j++) {
                char symbol;
                switch (board[i][j]) {
                    case HIT:
                        symbol = 'X';
                        break;
                    case MISS:
                        symbol = '.';
                        break;
                    case SHIP:
                        symbol = revealShips ? 'S' : '~';
                        break;
                    case EMPTY:
                    default:
                        symbol = '~';
                }
                System.out.print(" " + symbol);
            }
            System.out.println();
        }
    }
}
