package model;

import java.util.ArrayList;
import java.util.List;

// the 14x14 board - holds cells and validates placement rules
public class Board {
    private final int size = 14;
    private Cell[][] grid;

    public Board() {
        grid = new Cell[size][size];
        for (int row = 0; row < size; row++)
            for (int column = 0; column < size; column++)
                grid[row][column] = new Cell(row, column);
    }

    // copy constructor - AI uses this to simulate moves without changing the real
    // board
    public Board(Board other) {
        this();
        for (int row = 0; row < size; row++)
            for (int column = 0; column < size; column++)
                if (other.grid[row][column].isOccupied())
                    this.grid[row][column].setOccupied(true, other.grid[row][column].getPlayerId());
    }

    public int getSize() {
        return size;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    // checks all blokus rules: in bounds, no overlap, no side-touch, corner-touch
    // required
    public boolean isValidMove(Piece piece, Position startPosition, Player player) {
        Shape shape = piece.getShape();
        boolean hasCornerTouch = false;
        boolean isOnStartCorner = false;

        for (int row = 0; row < shape.rows(); row++) {
            for (int column = 0; column < shape.cols(); column++) {
                if (shape.cellAt(row, column) == 1) {
                    int boardRow = startPosition.getRow() + row;
                    int boardCol = startPosition.getColumn() + column;

                    if (boardRow < 0 || boardRow >= size || boardCol < 0 || boardCol >= size)
                        return false;
                    if (grid[boardRow][boardCol].isOccupied())
                        return false;
                    if (hasOrthogonalNeighbor(boardRow, boardCol, player.getId()))
                        return false;
                    if (hasDiagonalNeighbor(boardRow, boardCol, player.getId()))
                        hasCornerTouch = true;
                    if (isStartCorner(boardRow, boardCol))
                        isOnStartCorner = true;
                }
            }
        }
        return player.isFirstMove() ? isOnStartCorner : hasCornerTouch;
    }

    // the two fixed starting spots for blokus duo
    private boolean isStartCorner(int row, int column) {
        return (row == 4 && column == 4) || (row == 9 && column == 9);
    }

    private boolean hasOrthogonalNeighbor(int row, int column, int playerId) {
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        for (int[] direction : directions) {
            int neighborRow = row + direction[0];
            int neighborCol = column + direction[1];
            if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size)
                if (grid[neighborRow][neighborCol].isOccupied()
                        && grid[neighborRow][neighborCol].getPlayerId() == playerId)
                    return true;
        }
        return false;
    }

    private boolean hasDiagonalNeighbor(int row, int column, int playerId) {
        int[][] directions = { { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
        for (int[] direction : directions) {
            int neighborRow = row + direction[0];
            int neighborCol = column + direction[1];
            if (neighborRow >= 0 && neighborRow < size && neighborCol >= 0 && neighborCol < size)
                if (grid[neighborRow][neighborCol].isOccupied()
                        && grid[neighborRow][neighborCol].getPlayerId() == playerId)
                    return true;
        }
        return false;
    }

    // place a piece on the board permanently
    public void placePiece(Piece piece, Position startPosition, int playerId) {
        Shape shape = piece.getShape();
        for (int row = 0; row < shape.rows(); row++)
            for (int column = 0; column < shape.cols(); column++)
                if (shape.cellAt(row, column) == 1)
                    grid[startPosition.getRow() + row][startPosition.getColumn() + column].setOccupied(true, playerId);
    }

    // returns all empty cells where the player can legally start their next piece
    public List<Position> getAvailableCorners(int playerId) {
        List<Position> corners = new ArrayList<>();
        for (int row = 0; row < size; row++)
            for (int column = 0; column < size; column++)
                if (!grid[row][column].isOccupied() && !hasOrthogonalNeighbor(row, column, playerId))
                    if (hasDiagonalNeighbor(row, column, playerId) || isStartCorner(row, column))
                        corners.add(new Position(row, column));
        return corners;
    }
}
