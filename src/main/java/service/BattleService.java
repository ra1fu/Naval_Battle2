package service;

import models.Ship;
import models.ShipCategory;

import java.util.*;

/**
 * Сервис, реализующий логику игры «Морской бой».
 * Теперь включает:
 * - Вывод поля, где вода отображается символами '~'.
 * - Возможность ручного размещения кораблей игроком.
 */
public class BattleService {
    public static final int BOARD_SIZE = 10;

    /**
     * Состояния ячеек поля.
     */
    public enum CellState {
        EMPTY,   // пустая клетка (вода)
        SHIP,    // клетка с кораблём
        HIT,     // попадание
        MISS     // промах
    }

    // Игровое поле
    private CellState[][] board;
    // Список кораблей (используем модель Ship)
    private List<Ship> ships;
    // Размеры кораблей (стандартное распределение: один 4-палубный, два 3-палубных, три 2-палубных, четыре 1-палубных)
    private final int[] shipSizes = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
    // Русские буквы для обозначения вертикалей (без Ё и Й)
    private final String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

    /**
     * Конструктор – инициализирует поле в состоянии пустоты.
     * Теперь корабли размещаются отдельно (например, вручную).
     */
    public BattleService() {
        board = new CellState[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            Arrays.fill(board[i], CellState.EMPTY);
        }
        ships = new ArrayList<>();
    }

    /**
     * Позволяет игроку вручную разместить все корабли.
     * Для каждого корабля запрашиваются начальная координата и ориентация.
     *
     * @param scanner объект Scanner для ввода игроком
     */
    public void placeAllShipsManually(Scanner scanner) {
        System.out.println("Ручное размещение кораблей. Игроку необходимо разместить следующие корабли:");
        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                printBoard(true); // показываем текущее состояние поля с уже размещёнными кораблями
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
                int orientation; // 0 - горизонтально, 1 - вертикально
                if (orientInput.equals("H")) {
                    orientation = 0;
                } else if (orientInput.equals("V")) {
                    orientation = 1;
                } else {
                    System.out.println("Неверная ориентация. Попробуйте ещё раз.");
                    continue;
                }

                // Проверка, что корабль влезает в поле
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

                // Размещаем корабль на поле
                List<String> shipCoords = new ArrayList<>();
                if (orientation == 0) { // горизонтально
                    for (int j = col; j < col + size; j++) {
                        board[row][j] = CellState.SHIP;
                        shipCoords.add(convertToCoordinate(row, j));
                    }
                } else { // вертикально
                    for (int i = row; i < row + size; i++) {
                        board[i][col] = CellState.SHIP;
                        shipCoords.add(convertToCoordinate(i, col));
                    }
                }
                // Определяем категорию корабля по размеру
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

    /**
     * Проверяет корректность предлагаемого размещения корабля.
     * Для каждой клетки, которую займёт корабль, проверяются все соседние (по 8 направлений).
     *
     * @param row         начальная строка
     * @param col         начальный столбец
     * @param orientation 0 – горизонтальное, 1 – вертикальное
     * @param size        размер корабля
     * @return true, если размещение корректно, иначе false
     */
    private boolean isValidPlacement(int row, int col, int orientation, int size) {
        List<int[]> indices = new ArrayList<>();
        if (orientation == 0) { // горизонтальное
            for (int j = col; j < col + size; j++) {
                indices.add(new int[]{row, j});
            }
        } else { // вертикальное
            for (int i = row; i < row + size; i++) {
                indices.add(new int[]{i, col});
            }
        }
        for (int[] index : indices) {
            int r = index[0];
            int c = index[1];
            // Проверяем каждую клетку и её соседей
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

    /**
     * Преобразует индексы строки и столбца в строковое обозначение (например, 0,0 → "А1").
     *
     * @param row индекс строки (0–9)
     * @param col индекс столбца (0–9)
     * @return строка с координатой
     */
    private String convertToCoordinate(int row, int col) {
        return letters[col] + (row + 1);
    }

    /**
     * Преобразует строку координаты (например, "А5") в массив из {row, col}.
     *
     * @param coordinate строковое обозначение координаты
     * @return массив из двух элементов: {row, col}
     */
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

    /**
     * Выполняет выстрел по заданной координате (например, "А5").
     *
     * @param coordStr координата выстрела
     * @return результат выстрела: "Hit", "Miss", "Sunk", "Already Shot" или "Invalid"
     */
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

    /**
     * Если корабль потоплен, отмечает все соседние клетки (по 8 направлений) как MISS,
     * поскольку по правилам корабли не могут соприкасаться.
     *
     * @param ship потопленный корабль
     */
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

    /**
     * Проверяет, потоплены ли все корабли.
     *
     * @return true, если все корабли уничтожены, иначе false
     */
    public boolean isGameOver() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Выводит текущее состояние игрового поля.
     *
     * @param revealShips если true, отображает расположение кораблей (S), иначе показывает воду как '~'
     */
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
