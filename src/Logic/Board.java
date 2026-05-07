package Logic;

import java.util.ArrayList;
import java.util.List;

// the 14x14 game board - holds all cells and checks the rules
public class Board {
    private final int size = 14;
    private Cell[][] grid;

    // create an empty board
    public Board() {
        grid = new Cell[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                grid[row][column] = new Cell(row, column);
            }
        }
    }

    // copy constructor - used by AI to simulate moves without changing the real board
    public Board(Board other) {
        this();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (other.grid[row][column].isOccupied()) {
                    this.grid[row][column].setOccupied(true, other.grid[row][column].getPlayerId());
                }
            }
        }
    }

    public int getSize() { return size; }
    public Cell[][] getGrid() { return grid; }

    // checks all blokus rules: in bounds, no overlap, no side touch, corner touch required
    public boolean isValidMove(Piece piece, int startRow, int startCol, Player player) {
        Shape shape = piece.getShape();
        boolean hasCornerTouch = false;
        boolean isOnStartCorner = false;

        for (int row = 0; row < shape.rows(); row++) {
            for (int column = 0; column < shape.cols(); column++) {
                if (shape.cellAt(row, column) == 1) {
                    int boardRow = startRow + row;
                    int boardCol = startCol + column;

                    // piece must stay inside the board
                    if (boardRow < 0 || boardRow >= size || boardCol < 0 || boardCol >= size) {
                        return false;
                    }

                    // piece can't overlap another piece
                    if (grid[boardRow][boardCol].isOccupied()) {
                        return false;
                    }

                    // piece can't touch own pieces on the sides
                    if (hasOrthogonalNeighbor(boardRow, boardCol, player.getId())) {
                        return false;
                    }

                    // piece must touch own pieces at a corner
                    if (hasDiagonalNeighbor(boardRow, boardCol, player.getId())) {
                        hasCornerTouch = true;
                    }

                    // blokus duo starting corners are (4,4) for player 1 and (9,9) for player 2
                    if (isStartCorner(boardRow, boardCol)) {
                        isOnStartCorner = true;
                    }
                }
            }
        }

        // on first move the piece just needs to cover a start corner
        if (player.isFirstMove()) {
            return isOnStartCorner;
        }

        return hasCornerTouch;
    }

    // the two fixed starting spots for blokus duo
    private boolean isStartCorner(int row, int column) {
        return (row == 4 && column == 4) || (row == 9 && column == 9);
    }

    // returns true if any cell directly next to (row, column) belongs to the same player
    private boolean hasOrthogonalNeighbor(int row, int column, int playerId) {
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1} };
        for (int[] direction : directions) {
            int neighborRow = row + direction[0];
            int neighborCol = column + direction[1];
            if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size) {
                if (grid[neighborRow][neighborCol].isOccupied() && grid[neighborRow][neighborCol].getPlayerId() == playerId) {
                    return true;
                }
            }
        }
        return false;
    }

    // returns true if any diagonal neighbor belongs to the same player
    private boolean hasDiagonalNeighbor(int row, int column, int playerId) {
        int[][] directions = { {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };
        for (int[] direction : directions) {
            int neighborRow = row + direction[0];
            int neighborCol = column + direction[1];
            if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size) {
                if (grid[neighborRow][neighborCol].isOccupied() && grid[neighborRow][neighborCol].getPlayerId() == playerId) {
                    return true;
                }
            }
        }
        return false;
    }

    // put a piece on the board permanently
    public void placePiece(Piece piece, int startRow, int startCol, int playerId) {
        Shape shape = piece.getShape();
        for (int row = 0; row < shape.rows(); row++) {
            for (int column = 0; column < shape.cols(); column++) {
                if (shape.cellAt(row, column) == 1) {
                    grid[startRow + row][startCol + column].setOccupied(true, playerId);
                }
            }
        }
    }

    // finds all empty cells where the player can legally place their next piece (corner candidates)
    public List<int[]> getAvailableCorners(int playerId) {
        List<int[]> corners = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (!grid[row][column].isOccupied() && !hasOrthogonalNeighbor(row, column, playerId)) {
                    if (hasDiagonalNeighbor(row, column, playerId) || isStartCorner(row, column)) {
                        corners.add(new int[]{row, column});
                    }
                }
            }
        }
        return corners;
    }

    // prints the board to the console - useful for debugging
    public void printBoard() {
        System.out.println("  0 1 2 3 4 5 6 7 8 9 0 1 2 3");
        for (int row = 0; row < size; row++) {
            System.out.print((row % 10) + " ");
            for (int column = 0; column < size; column++) {
                if (!grid[row][column].isOccupied()) {
                    System.out.print(". ");
                } else {
                    int playerId = grid[row][column].getPlayerId();
                    System.out.print((playerId == 1 ? "X" : "O") + " ");
                }
            }
            System.out.println();
        }
    }
}